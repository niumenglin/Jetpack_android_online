package com.niu.jetpack_android_online.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.niu.jetpack_android_online.R
import com.niu.jetpack_android_online.databinding.LayoutAbsListLoadingFooterBinding
import com.niu.jetpack_android_online.databinding.LayoutAbsListLoadingFooterBinding.*

class FooterLoadStateAdapter : LoadStateAdapter<FooterLoadStateAdapter.LoadStateViewHolder>() {
    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        val loading = holder.binding.loading
        val loadingText = holder.binding.text
        when (loadState) {
            is LoadState.Loading -> {//加载中
                loading.show()
                loadingText.setText(R.string.abs_list_loading_footer_loading)
                return
            }
            is LoadState.Error -> {
                loadingText.setText(R.string.abs_list_loading_footer_error)
            }

            else -> {
            }
        }
        loading.hide()
        loading.postOnAnimation {
            loading.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadStateViewHolder {
        val binding = inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding)
    }

    inner class LoadStateViewHolder(val binding: LayoutAbsListLoadingFooterBinding) :
        RecyclerView.ViewHolder(binding.root)
}