package com.niu.jetpack_android_online.navigation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.niu.jetpack.plugin.runtime.NavDestination
import com.niu.jetpack_android_online.R
import com.niu.jetpack_android_online.base.BaseFragment
import com.niu.jetpack_android_online.databinding.LayoutFragmentCategoryBinding

@NavDestination(type = NavDestination.NavType.Fragment, route = "category_fragment")
class CategoryFragment : BaseFragment<LayoutFragmentCategoryBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "onCreate: $savedInstanceState")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navigateToTagsFragment.setOnClickListener {
            findNavController().navigateTo("tags_fragment")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("outState", "我是categoryFragment")
    }
}