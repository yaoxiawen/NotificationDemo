package com.example.notification

import android.app.Activity
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.exception.NoRouteFoundException
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.launcher.ARouter

/**
 * 获取ARouter path 对应的activity，找到对应的Activity执行回调
 */
fun getARouterActivityClass(path: String, block: (Postcard) -> Unit): Boolean {
    try {
        val postcard = ARouter.getInstance().build(path)
        LogisticsCenter.completion(postcard)
        if (postcard.type == RouteType.ACTIVITY) {
            block.invoke(postcard)
            return true
        }
    } catch (ex: NoRouteFoundException) {
        ex.printStackTrace()
    }
    return false
}

fun getActivityByARouter(path: String): Class<*>? {
    try {
        val postcard = ARouter.getInstance().build(path)
        LogisticsCenter.completion(postcard)
        if (postcard.type == RouteType.ACTIVITY) {
            return postcard.destination
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return null
}

/**
 * 当前activity是否对应path
 */
fun isActivityJustThePath(activity: Activity?, path: String?): Boolean {
    if (activity == null || path.isNullOrEmpty()) {
        return false
    }
    val pathClass = getActivityByARouter(path) ?: return false
    return activity.javaClass == pathClass
}