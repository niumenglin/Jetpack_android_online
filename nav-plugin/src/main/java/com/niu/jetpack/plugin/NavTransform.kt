package com.niu.jetpack.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.niu.jetpack.plugin.runtime.NavData
import com.niu.jetpack.plugin.runtime.NavDestination
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.apache.commons.io.FileUtils
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AnnotationNode
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.ZipFile

class NavTransform(private val project: Project) : Transform() {
    private val navDatas = mutableListOf<NavData>()

    companion object {
        const val NAV_RUNTIME_DESTINATION = "Lcom/niu/jetpack/plugin/runtime/NavDestination;"
        const val NAV_RUNTIME_TYPE = "Lcom/niu/jetpack/plugin/runtime/NavDestination\$NavType"

        private const val KEY_ROUTE = "route"
        private const val KEY_TYPE = "type"
        private const val KEY_STARTER = "asStarter"

        private const val NAV_RUNTIME_PKG_NAME: String = "com.niu.jetpack.plugin.runtime"
        private const val NAV_RUNTIME_REGISTRY_CLASS_NAME: String = "NavRegistry"
        private const val NAV_RUNTIME_NAV_DATA_CLASS_NAME: String = "NavData"
        private const val NAV_RUNTIME_NAV_LIST: String = "navList"
        private const val NAV_RUNTIME_MODULE_NAME: String = "nav-plugin-runtime"
    }

    override fun getName(): String {
        return "NavTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        val inputs = transformInvocation?.inputs ?: return
        val outputProvider = transformInvocation.outputProvider
        outputProvider.deleteAll()
        inputs.forEach { it ->
            //1.对inputs --> directory --> class 文件进行遍历
            //2.对inputs -->       jar --> class 文件进行遍历
            it.directoryInputs.forEach {
                handleDirectoryClasses(it.file)
                //获取输出目录
                val outputDir = outputProvider.getContentLocation(
                    it.name,
                    it.contentTypes,
                    it.scopes, //作用域
                    Format.DIRECTORY //文件类型
                )
                if (it.file.isFile) {
                    //将文件copy到输出目录
                    FileUtils.copyFile(it.file, outputDir)
                } else {
                    FileUtils.copyDirectory(it.file, outputDir)
                }

            }
            it.jarInputs.forEach {
                handleJarClasses(it.file)
                //获取输出目录
                val outputDir = outputProvider.getContentLocation(
                    it.name,
                    it.contentTypes,
                    it.scopes, //作用域
                    Format.JAR //文件类型
                )
                FileUtils.copyFile(it.file, outputDir)
            }
        }
        generateNavRegistry()
    }

    //生成路由表文件
    private fun generateNavRegistry() {
        // 利用kotlinPoet生成NavRegistry.kt文件，存放在nav-plugin-runtime模块下；
        // 用于记录项目中所有的路由节点数据

        //1、生成成员变量 val navList: ArrayList<NavData>
        val navData = ClassName(NAV_RUNTIME_PKG_NAME, NAV_RUNTIME_NAV_DATA_CLASS_NAME)
        val arrayList = ClassName("kotlin.collections", "ArrayList")

        //2、生成get 方法返回值类型List<NavData>
        val list = ClassName("kotlin.collections", "List")
        val arrayListOfNavData = arrayList.parameterizedBy(navData)
        val listOfNavData = list.parameterizedBy(navData)

        //3、生成 object class init{ 代码块 }
        val statements = java.lang.StringBuilder()
        navDatas.forEach {
            statements.append(
                String.format(
                    "navList.add(NavData(\"%s\",\"%s\",%s,%s))",
                    it.route,
                    it.className,
                    it.asStarter,
                    it.type
                )
            )
            statements.append("\n")
        }

        //4、向object class 添加成员属性 navList 并且进行初始化赋值
        val property =
            PropertySpec.builder(NAV_RUNTIME_NAV_LIST, arrayListOfNavData, KModifier.PRIVATE)
                .initializer(CodeBlock.builder().addStatement("ArrayList<NavData>()").build())
                .build()

        //5、构建get方法，并且生成代码块
        val function = FunSpec.builder("get")
            .returns(listOfNavData)
            .addCode(
                CodeBlock.builder()
                    .addStatement("val list = ArrayList<NavData>()\n list.addAll(navList)\n return list\n")
                    .build()
            )
            .build()

        //6、构建object NavRegistry class，并且填充属性、init{}、get方法
        val typeSpec = TypeSpec.objectBuilder(NAV_RUNTIME_REGISTRY_CLASS_NAME)
            .addProperty(property)
            .addInitializerBlock(CodeBlock.builder().addStatement(statements.toString()).build())
            .addFunction(function)
            .build()

        //7.生成文件、添加注释和导包
        val fileSpec = FileSpec.builder(NAV_RUNTIME_PKG_NAME, NAV_RUNTIME_REGISTRY_CLASS_NAME)
            .addComment("this file is generated by auto.please do not modify!!!")
            .addType(typeSpec)
            .addImport(NavDestination.NavType::class.java, "Fragment", "Dialog", "Activity", "None")
            .build()

        //8、写入文件
        val runtimeProject = project.rootProject.findProject(NAV_RUNTIME_MODULE_NAME)
        assert(runtimeProject == null) {
            throw GradleException("cannot found $NAV_RUNTIME_MODULE_NAME")
        }
        val sourceSets = runtimeProject!!.extensions.findByName("sourceSets") as SourceSetContainer
        //com.niu.jetpack.runtime
        val outputFileDir = sourceSets.first().java.srcDirs.first().absoluteFile
        println("NavTransform outputFileDir:${outputFileDir.absolutePath}")
        fileSpec.writeTo(outputFileDir)

    }

    private fun handleDirectoryClasses(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                handleDirectoryClasses(it)
            }
        } else if (file.extension.endsWith("class", true)) {
            println("NavTransform handleDirectoryClasses-filename:${file.name}")
            val inputStream = FileInputStream(file)
            visitClass(inputStream)
            inputStream.close()
        }
    }

    private fun handleJarClasses(file: File) {
        println("NavTransform handleJarClasses:${file.name}")
        val zipFile = ZipFile(file)
        zipFile.stream().forEach {
            if (it.name.endsWith("class", true)) {
                println("NavTransform handleJarClasses-zipEntry:${it.name}")
                val inputStream = zipFile.getInputStream(it)
                visitClass(inputStream)
                inputStream.close()
            }
        }
        zipFile.close()
    }

    private fun visitClass(inputStream: InputStream) {
        val classReader = ClassReader(inputStream)
        val classVisitor = object : ClassVisitor(Opcodes.ASM9) {
            override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
//                return super.visitAnnotation(descriptor, visible)

                println("visitAnnotation1:$descriptor")
                println("visitAnnotation2:$NAV_RUNTIME_DESTINATION")
                if (descriptor != NAV_RUNTIME_DESTINATION) {
                    return object : AnnotationVisitor(Opcodes.ASM9) {
                    }
                }
                val annotationVisitor = object : AnnotationNode(Opcodes.ASM9, "") {
                    var route = ""
                    var asStarter = false
                    var type = NavDestination.NavType.None

                    //基础类型
                    override fun visit(name: String?, value: Any?) {
                        super.visit(name, value)
                        if (name == KEY_ROUTE) {
                            route = value as String
                        } else if (name == KEY_STARTER) {
                            asStarter = value as Boolean
                        }
                    }

                    //枚举类型
                    override fun visitEnum(name: String?, descriptor: String?, value: String?) {
                        super.visitEnum(name, descriptor, value)
                        if (name == KEY_TYPE) {
                            assert(value == null) {
                                throw GradleException("NavDestination$type must be one of Fragment,Activity,Dialog")
                            }
                            type = NavDestination.NavType.valueOf(value!!)
                        }
                    }

                    override fun visitEnd() {
                        super.visitEnd()
                        val navData = NavData(route, classReader.className.replace("/", "."),asStarter, type)
                        navDatas.add(navData)
                    }
                }
                return annotationVisitor
            }
        }

        classReader.accept(classVisitor, EXPAND_FRAMES)
    }
}