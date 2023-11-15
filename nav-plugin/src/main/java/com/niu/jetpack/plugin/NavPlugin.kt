package com.niu.jetpack.plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin


class NavPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("NavPlugin apply...")

        val appPlugin = project.plugins.findPlugin(ApplicationPlugin::class.java)
        assert(appPlugin == null) {
            throw GradleException("NavPlugin can only be applied to app module")
        }

        val extensions = project.extensions.findByType(BaseExtension::class.java)
        extensions?.registerTransform(NavTransform(project))
    }

}