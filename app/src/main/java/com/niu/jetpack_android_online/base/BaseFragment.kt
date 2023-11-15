package com.niu.jetpack_android_online.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

open class BaseFragment<T : ViewBinding> : Fragment() {

    private val TAG = this::class.java.simpleName + "-" + this.hashCode()
    lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("fragmentLife", "${TAG}-onCreate: ")
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("fragmentLife", "${TAG}-onCreateView: ")
        val type = javaClass.genericSuperclass as ParameterizedType
        val aClass = type.actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod("inflate",LayoutInflater::class.java,ViewGroup::class.java,Boolean::class.java)
        binding = method.invoke(null,layoutInflater,container,false) as T
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.e("fragmentLife", "${TAG}-onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.e("fragmentLife", "${TAG}-onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.e("fragmentLife", "${TAG}-onStop: ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("fragmentLife", "${TAG}-onDestroyView: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("fragmentLife", "${TAG}-onDestroy: ")
    }

}