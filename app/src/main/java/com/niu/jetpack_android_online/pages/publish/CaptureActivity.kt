package com.niu.jetpack_android_online.pages.publish

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.niu.jetpack_android_online.R
import com.niu.jetpack_android_online.base.BaseActivity
import com.niu.jetpack_android_online.databinding.ActivityLayoutCaptureBinding

class CaptureActivity : BaseActivity<ActivityLayoutCaptureBinding>() {

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

    }

    companion object {
        private const val TAG = "CaptureActivity"

        //动态权限申请
        private val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) Manifest.permission.WRITE_EXTERNAL_STORAGE else null
        )

        private const val REQ_CAPTURE = 10001
        private const val PERMISSION_CODE = 1000
    }
}