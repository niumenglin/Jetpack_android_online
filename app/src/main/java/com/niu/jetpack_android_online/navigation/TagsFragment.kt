package com.niu.jetpack_android_online.navigation

import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.niu.jetpack.plugin.runtime.NavDestination
import com.niu.jetpack_android_online.R
import com.niu.jetpack_android_online.base.BaseFragment
import com.niu.jetpack_android_online.databinding.LayoutFragmentTagsBinding

@NavDestination(type = NavDestination.NavType.Fragment, route = "tags_fragment")
class TagsFragment : BaseFragment<LayoutFragmentTagsBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //homeFragment--categoryFragment--tagsFragment--userFragment
        //------------------------------NavOptions
        binding.navigateToUserFragment.setOnClickListener {
//            findNavController().navigate(R.id.user_fragment)
//            findNavController().navigate(
//                R.id.user_fragment, null, NavOptions.Builder()
//                    .setPopUpTo(R.id.category_fragment, inclusive = true, saveState = true)
//                    .build()TA
//            )

//            findNavController().popBackStack(
//                R.id.home_fragment,
//                inclusive = false,
//                saveState = true
//            )
            findNavController().navigateBack("home_fragment",inclusive = false,saveState = true)
        }
    }
}