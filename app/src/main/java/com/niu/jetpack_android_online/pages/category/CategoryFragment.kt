package com.niu.jetpack_android_online.pages.category

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.niu.jetpack.plugin.runtime.NavDestination
import com.niu.jetpack_android_online.base.BaseFragment
import com.niu.jetpack_android_online.databinding.LayoutFragmentCategoryBinding
import com.niu.jetpack_android_online.navigation.navigateTo
import com.niu.jetpack_android_online.pages.home.HomeFragment
import com.niu.jetpack_android_online.utils.AppConfig

@NavDestination(type = NavDestination.NavType.Fragment, route = "category_fragment")
class CategoryFragment : BaseFragment<LayoutFragmentCategoryBinding>() {
    private lateinit var mediator: TabLayoutMediator
    private val categoryConfig = AppConfig.getCategory()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        //viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        //关闭ViewPager2预加载效果
        (viewPager.getChildAt(0) as RecyclerView).layoutManager?.isItemPrefetchEnabled = false
        viewPager.adapter = object : FragmentStateAdapter(childFragmentManager, this.lifecycle) {
            override fun getItemCount(): Int {
                return categoryConfig.tabs!!.size
            }

            override fun createFragment(position: Int): Fragment {
                val tag = categoryConfig.tabs!![position].tag
                return HomeFragment.newInstance(tag)
            }

        }

        tabLayout.tabGravity = categoryConfig.tabGravity
        mediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.customView = makeTabView(position)
        }
        mediator.attach()//tabLayout与viewPager2滑动联动

        viewPager.registerOnPageChangeCallback(pageChangeCallback)

        viewPager.post {
            viewPager.currentItem = categoryConfig.select
        }
    }

    private val pageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val childCount = binding.tabLayout.childCount
            for (i in 0 until childCount) {
                val tab = binding.tabLayout.getTabAt(i)
                val customView = tab!!.customView as TextView
                if (tab.position == position) {
                    customView.textSize = categoryConfig.activeSize.toFloat()
                    customView.typeface = Typeface.DEFAULT_BOLD
                } else {
                    customView.textSize = categoryConfig.normalSize.toFloat()
                    customView.typeface = Typeface.DEFAULT
                }
            }
        }
    }

    private fun makeTabView(position: Int): View? {
        val tabView = TextView(context)
        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(android.R.attr.state_selected)
        states[1] = intArrayOf()

        val colors = intArrayOf(
            Color.parseColor(categoryConfig.activeColor),
            Color.parseColor(categoryConfig.normalColor)
        )
        val stateList = ColorStateList(states, colors)
        tabView.setTextColor(stateList)
        tabView.text = categoryConfig.tabs!![position].title
        tabView.textSize = categoryConfig.normalSize.toFloat()
        tabView.gravity = Gravity.CENTER

        return tabView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediator.detach() //tabLayout与viewPage2反向关联
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }
}