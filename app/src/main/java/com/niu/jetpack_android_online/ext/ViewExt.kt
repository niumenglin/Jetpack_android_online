package com.niu.jetpack_android_online.ext

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.button.MaterialButton
import com.niu.jetpack_android_online.utils.PixUtil
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

fun View.setVisibility(condition: Boolean) {
    this.visibility = if (condition) View.VISIBLE else View.GONE
}

fun TextView.setTextVisibility(content: String?, goneWhenNull: Boolean = true) {
    if (TextUtils.isEmpty(content) && goneWhenNull) {
        visibility = View.GONE
        return
    }
    visibility = View.VISIBLE
    text = content
}

fun ImageView.setImageResource(condition: Boolean, trueRes: Int, falseRes: Int) {
    setImageResource(if (condition) trueRes else falseRes)
}

@SuppressLint("CheckResult")
fun ImageView.setImageUrl(imageUrl: String?, isCircle: Boolean = false, radius: Int = 0) {
    if (TextUtils.isEmpty(imageUrl)) {
        visibility = View.GONE
        return
    }
    visibility = View.VISIBLE
    val builder = Glide.with(this).load(imageUrl)
    if (isCircle) {
        builder.transform(CircleCrop())
    } else if (radius > 0) {
        builder.transform(RoundedCornersTransformation(PixUtil.dp2px(radius), 0))
    }
    val layoutParams = this.layoutParams
    if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
        builder.override(layoutParams.width, layoutParams.height)
    }
    builder.into(this)
}

fun ImageView.load(imageUrl: String, callback: (Bitmap) -> Unit) {
    Glide.with(this).asBitmap().load(imageUrl).into(object : BitmapImageViewTarget(this) {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            super.onResourceReady(resource, transition)
            callback(resource)
        }
    })
}

//高斯模糊-Glide
fun ImageView.setBlurImageUrl(blurUrl: String, radius: Int) {
    Glide.with(this).load(blurUrl).override(radius)
        .transform(BlurTransformation()).dontAnimate().into(object : DrawableImageViewTarget(this) {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                super.onResourceReady(resource, transition)
                background = resource
            }
        })
}

fun MaterialButton.setIconResource(condition: Boolean, trueRes: Int, falseRes: Int) {
    setIconResource(if (condition) trueRes else falseRes)
}