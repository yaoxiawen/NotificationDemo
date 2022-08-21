package com.example.notification

import android.net.Uri
import com.alibaba.android.arouter.launcher.ARouter

object LinkManager {

    fun handleLink(link: String?) {
        val uri = Uri.parse(link)
        val path = uri.path
        val names = uri.queryParameterNames
        val postcard = ARouter.getInstance()
            .build(path)
        for (key in names) {
            postcard.withString(key, uri.getQueryParameter(key))
        }
        postcard.navigation()
    }

}