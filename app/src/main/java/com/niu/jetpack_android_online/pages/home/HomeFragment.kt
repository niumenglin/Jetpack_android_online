package com.niu.jetpack_android_online.pages.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.niu.jetpack.plugin.runtime.NavDestination
import com.niu.jetpack_android_online.ext.invokeViewModel
import com.niu.jetpack_android_online.list.AbsListFragment
import kotlinx.coroutines.launch

@NavDestination(type = NavDestination.NavType.Fragment, route = "home_fragment", asStarter = true)
class HomeFragment : AbsListFragment() {
    private val viewModel: HomeViewModel by invokeViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //启动协程
        lifecycleScope.launch {
            viewModel.hotFeeds.collect {
                submitData(it)
            }
        }
    }
}