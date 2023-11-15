package com.niu.jetpack_android_online.navigation

import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.niu.jetpack.plugin.runtime.NavDestination
import com.niu.jetpack_android_online.R
import com.niu.jetpack_android_online.base.BaseFragment
import com.niu.jetpack_android_online.databinding.LayoutFragmentUserBinding

@NavDestination(type = NavDestination.NavType.Fragment, route = "user_fragment")
class UserFragment : BaseFragment<LayoutFragmentUserBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backToPre.setOnClickListener {
//            findNavController().navigate(R.id.category_fragment,null,NavOptions.Builder().setRestoreState(true).build())
//            findNavController().navigate(R.id.user_fragment,null,
//                NavOptions.Builder()
//                    .setLaunchSingleTop(true)
//                    .setRestoreState(true)
//                    .build())
            findNavController().popBackStack(
                R.id.home_fragment,
                inclusive = false,
                saveState = true)
        }
    }
}