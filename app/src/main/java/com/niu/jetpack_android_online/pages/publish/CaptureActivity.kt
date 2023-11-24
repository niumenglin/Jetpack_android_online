package com.niu.jetpack_android_online.pages.publish

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.niu.jetpack.plugin.runtime.NavDestination
import com.niu.jetpack_android_online.R
import com.niu.jetpack_android_online.base.BaseActivity
import com.niu.jetpack_android_online.databinding.ActivityLayoutCaptureBinding
import com.niu.jetpack_android_online.ext.setVisibility
import com.niu.jetpack_android_online.utils.showToast
import java.lang.Exception
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("RestrictedApi")
@NavDestination(route = "activity_capture", type = NavDestination.NavType.Activity)
class CaptureActivity : BaseActivity<ActivityLayoutCaptureBinding>() {

    private lateinit var imageCapture: ImageCapture
    private lateinit var camera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            val deniedPermissions = mutableListOf<String>()
            for (i in PERMISSIONS.indices) {
                val permission = permissions[i]
                val result = grantResults[i]
                if (result != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission)
                }
            }

            if (deniedPermissions.isEmpty()) {
                //全部授权成功
                startCamera()
            } else {
                AlertDialog.Builder(this)
                    .setMessage(getString(R.string.capture_permission_message))
                    .setNegativeButton(getString(R.string.capture_permission_no)) { dialog, _ ->
                        dialog.dismiss()
                        this@CaptureActivity.finish()
                    }.setPositiveButton(getString(R.string.capture_permission_ok)) { dialog, _ ->
                        //申请被拒绝的权限
                        ActivityCompat.requestPermissions(
                            this@CaptureActivity,
                            deniedPermissions.toTypedArray(),
                            PERMISSION_CODE
                        )
                        dialog.dismiss()
                    }.create().show()
            }
        }
    }

    //开启相机以及预览能力
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            // 创建ProcessCameraProvider实例
            // 用于将相机的生命周期绑定到生命周期所有者
            // 这消除了打开和关闭相机的任务，因为CameraX具有生命周期感知能力
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = when {
                cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) -> CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) -> CameraSelector.DEFAULT_FRONT_CAMERA
                else -> throw IllegalStateException("Back and Front camera are unavailable")
            }
            val displayRotation = binding.previewView.display.rotation

            // preview use case
            val preview = Preview.Builder()
                .setCameraSelector(cameraSelector)
                .setTargetRotation(displayRotation)
                .build().also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // imageCapture 图片拍摄
            val imageCapture = ImageCapture.Builder()
                .setTargetRotation(displayRotation)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)//优化图片质量，压缩
//                .setTargetRotation(16/9)// 设置期望的宽高比 16:9, 4:3
                .setJpegQuality(90)// 设置期望的图片质量0-100
                // 设置期望的的最大的分辨率，拍摄出来的图片分辨率不会高于1080,1920
                // 和setTargetAspectRatio不能同时设置，只能二选一
                .setResolutionSelector(
                    ResolutionSelector.Builder().setMaxResolution(Size(1920, 1080)).build()
                )
                .build()
            this.imageCapture = imageCapture
            try {
                cameraProvider.unbindAll()
                this.camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                bindUI()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindUI() {
        binding.recordView.setOnClickListener {
            takePicture()
        }

        binding.previewView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val meteringPointFactory = binding.previewView.meteringPointFactory
                val point = meteringPointFactory.createPoint(event.x, event.y)
                val focusAction =
                    FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF).build()
                this@CaptureActivity.camera.cameraControl.startFocusAndMetering(focusAction)
                showFocusPoint(event.x, event.y)
            }
            true
        }
    }

    private fun showFocusPoint(x: Float, y: Float) {
        val focusView = binding.focusPoint
        val alphaAnim = SpringAnimation(focusView, DynamicAnimation.ALPHA, 1f).apply {
            spring.stiffness = SPRING_STIFFNESS
            spring.dampingRatio = SPRING_DAMPING_RATIO
            addEndListener { _, _, _, _ ->
                SpringAnimation(focusView, DynamicAnimation.ALPHA, 0f).apply {
                    spring.stiffness = SPRING_STIFENESS_ALPHA_OUT
                    // 阻尼系数
                    spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                }.start()
            }
        }

        val scaleXAnim = SpringAnimation(focusView, DynamicAnimation.SCALE_X, 1f).apply {
            spring.stiffness = SPRING_STIFFNESS
            spring.dampingRatio = SPRING_DAMPING_RATIO
        }
        val scaleYAnim = SpringAnimation(focusView, DynamicAnimation.SCALE_Y, 1f).apply {
            spring.stiffness = SPRING_STIFFNESS
            spring.dampingRatio = SPRING_DAMPING_RATIO
        }

        focusView.bringToFront()
        focusView.setVisibility(true)
        focusView.translationX = x - focusView.width / 2
        focusView.translationY = y - focusView.height / 2
        focusView.alpha = 0f
        focusView.scaleX = 1.5f
        focusView.scaleY = 1.5f

        alphaAnim.start()
        scaleXAnim.start()
        scaleYAnim.start()
    }

    private fun takePicture() {
        val vibrator = getSystemService(Vibrator::class.java) as Vibrator
        vibrator.vibrate(200)

        // 文件名
        val fileName = SimpleDateFormat(FILENAME, Locale.CHINA).format(System.currentTimeMillis())
        // 存放位置
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, PHOTO_TYPE)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, RELATIVE_PATH_PICTURE)
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object :
            ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                // 拍摄成功
                val savedUri = outputFileResults.savedUri
                Log.d(TAG, "onImageSaved capture success:${savedUri}")
                onFileSaved(savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                // 拍摄失败
                exception.imageCaptureError.showToast()
            }
        })
    }

    private fun onFileSaved(savedUri: Uri?) {

    }

    companion object {
        private const val TAG = "CaptureActivity"

        //动态权限申请
        private val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) Manifest.permission.WRITE_EXTERNAL_STORAGE else null
        ).filterNotNull().toTypedArray()

        // spring 动画参数配置
        private const val SPRING_STIFENESS_ALPHA_OUT = 100f
        private const val SPRING_STIFFNESS = 800f
        private const val SPRING_DAMPING_RATIO = 0.35f

        // 图片/视频文件名称，存放位置
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-sss"
        private const val PHOTO_TYPE = "image/jpeg"
        private const val VIDEO_TYPE = "video/mp4"
        private const val RELATIVE_PATH_PICTURE = "Pictures/Jetpack"
        private const val RELATIVE_PATH_VIDEO = "Movies/Jetpack"

        // request code
        private const val REQ_CAPTURE = 10001
        private const val PERMISSION_CODE = 1000
    }
}