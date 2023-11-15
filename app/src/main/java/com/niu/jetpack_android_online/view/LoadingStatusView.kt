package com.niu.jetpack_android_online.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import com.niu.jetpack_android_online.databinding.LayoutLoadingStatusViewBinding

//默认情况下展示进度条，当加载成功时隐藏
class LoadingStatusView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding =
        LayoutLoadingStatusViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        binding.loading.show()
    }

    @SuppressLint("ResourceType")
    fun showEmpty(@DrawableRes iconRes: Int, text: String, retry: OnClickListener?) {
        binding.loading.hide()
        binding.emptyLayout.visibility = View.VISIBLE
        if (iconRes > 0) {
            binding.emptyIcon.setImageResource(iconRes)
        }
        if (!TextUtils.isEmpty(text)) {
            binding.emptyText.text = text
            binding.emptyText.visibility = View.VISIBLE
        }
        retry?.run {
            binding.emptyAction.visibility = View.VISIBLE
            binding.emptyAction.setOnClickListener(this)
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility != View.VISIBLE) {
            binding.loading.hide()
        }
    }
}