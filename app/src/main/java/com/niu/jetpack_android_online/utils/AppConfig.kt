package com.niu.jetpack_android_online.utils

import android.content.Context
import com.alibaba.fastjson.JSON
import com.niu.jetpack_android_online.navigation.BottomBar
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object AppConfig {
    private var sBottomBar: BottomBar? = null
    fun getBottomBarConfig(context: Context): BottomBar {
        if (sBottomBar == null) {
            val content: String = parseFile(context, "main_tabs_config.json")
            sBottomBar = JSON.parseObject(content, BottomBar::class.java)
        }
        return sBottomBar!!
    }

    private fun parseFile(context: Context, fileName: String): String {
        val assets = context.assets
        var inputStream: InputStream? = null
        var br: BufferedReader? = null
        val builder = StringBuilder()
        try {
            inputStream = assets.open(fileName)
            br = BufferedReader(InputStreamReader(inputStream))
            var line: String? = null
            while (br.readLine().also { line = it } != null) {
                builder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
                br?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return builder.toString()
    }
}