package com.niu.jetpack_android_online.navigation

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.niu.jetpack.plugin.runtime.NavDestination
import com.niu.jetpack_android_online.R
import com.niu.jetpack_android_online.base.BaseFragment
import com.niu.jetpack_android_online.databinding.LayoutFragmentHomeBinding
import com.niu.jetpack_android_online.http.ApiService
import kotlinx.coroutines.launch
import kotlin.concurrent.thread
import kotlin.coroutines.suspendCoroutine

@NavDestination(type = NavDestination.NavType.Fragment, route = "home_fragment", asStarter = true)
class HomeFragment : BaseFragment<LayoutFragmentHomeBinding>() {
//    lateinit var homeBinding: LayoutFragmentHomeBinding
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        homeBinding = LayoutFragmentHomeBinding.inflate(inflater,container,false)
//        return homeBinding.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val apiResult = ApiService.getService().getFeeds()
            println("1111")
            apiResult
        }

        val navController = findNavController()
        binding.navigateToCategoryFragment.setOnClickListener {
            //对于fragment类型的路由节点，在 navigate 跳转的时候 使用的fragmentTransaction的replace()方法
//            navController.navigate(R.id.category_fragment)
//            navController.navigate(NavDeepLinkRequest.Builder
//                .fromUri(Uri.parse("https://com.niu.jetpack/user?phone=123456")).build())

            navController.navigateTo("category_fragment")
        }

        binding.navigateUp.setOnClickListener {
            navController.navigate(
                R.id.category_fragment, null,
                NavOptions.Builder()
                    .setRestoreState(true)
                    .build()
            )
//            navController.navigate(R.id.tags_fragment,null,
//                NavOptions.Builder()
//                    .setRestoreState(true)
//                    .build())
        }
    }
}