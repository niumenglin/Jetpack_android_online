package com.niu.jetpack_android_online

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.niu.jetpack_android_online.base.BaseActivity
import com.niu.jetpack_android_online.databinding.ActivityMainBinding
import com.niu.jetpack_android_online.navigation.NavGraphBuilder
import com.niu.jetpack_android_online.navigation.switchTab
import com.niu.jetpack_android_online.utils.AppConfig.getBottomBarConfig

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        NavGraphBuilder.build(navController, this)
        binding.appBottomBar.setOnItemSelectedListener {
//            navController.navigate(it.itemId)
            val tab = getBottomBarConfig(this).tabs!![it.order]
            navController.switchTab(tab.route!!)
            !TextUtils.isEmpty(it.title)
        }
    }
}