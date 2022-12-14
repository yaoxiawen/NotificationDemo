/**
 * Copyright (c) 2022, Lollitech
 * All rights reserved
 * author: funaihui@lollitech.com
 * describe: app相关工具类
 **/
@file: JvmName("ToolApp")

package com.lollypop.android.tool

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Looper
import android.os.Process
import java.util.*
import kotlin.system.exitProcess


/**
 * 获取当前app版本信息
 *
 * @return 当前版本名称，获取不到时为 ""
 */
fun Context.getVersionName(): String {
    return getPackageInfo()?.versionName ?: ""
}

/**
 * 获取当前app版本号
 *
 * @return 当前版本号
 */
fun Context.getVersionCode(): Int {
    return getPackageInfo()?.versionCode ?: 0
}

/**
 * 生成唯一code
 *
 * @return 唯一code
 */
fun generateUniqueCode(): String = UUID.randomUUID().toString()

/**
 * 是否App进程启动
 *
 * @return true:是
 */
fun Context?.isMyAppProcess(): Boolean {
    if (this == null) {
        return false
    }

    val processName = getProcessName(Process.myPid())
    return processName.isEmpty() || processName == packageName
}

/**
 * 根据Pid获取进程名称
 *
 * @param pid     进程id
 * @return 进程名称 如果没有获取到则为空
 */
fun Context?.getProcessName(pid: Int): String {
    val processInfo = getMatchedAppProcessInfo { it.pid == pid }
    return processInfo?.processName ?: ""
}

/**
 * 在进程中去寻找当前APP的信息，判断是否在前台运行
 *
 * @return true:当前app正在前台；false：当前app在后台
 */
fun Context?.isAppOnForeground(): Boolean {
    val processInfo = getMatchedAppProcessInfo {
        it.processName == this?.packageName
                && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }
    return processInfo != null
}

/**
 * 判断是否存在某程序,要判断某个程序，需要在清单文件中声明，否则Android11后会有问题
 *
 * @param packageName 包名
 * @return 对应包名程序安装时返回true，否则返回false
 */
fun Context?.isPackageInstalled(packageName: String?): Boolean {
    if (this == null || packageName.isNullOrEmpty()) {
        return false
    }

    return getPackageInfo(packageName) != null
}

/**
 * Activity 是否Activity生命周期结束
 *
 * @return true：已经结束
 */
fun Activity?.isActivityFinishing() = this == null || isDestroyed

/**
 * 是否UI线程
 *
 * @return true:UI线程
 */
fun isUIThread(): Boolean = Thread.currentThread() === Looper.getMainLooper().thread

/**
 * 立即杀死App
 */
fun killAppNow() {
    Process.killProcess(Process.myPid())
    exitProcess(0)
}

/**
 * 获取packageName对应的PackageInfo
 *
 * @param packageName 包名称
 *
 * @return PackageInfo 可能为空
 */
private fun Context.getPackageInfo(packageName: String): PackageInfo? {
    return runCatching {
        packageManager.getPackageInfo(packageName, 0)
    }.getOrNull()
}

/**
 * 获取当前Context对应的PackageInfo
 *
 * @return PackageInfo 可能为空
 */
private fun Context.getPackageInfo() = getPackageInfo(packageName)

/**
 * 获取匹配的进程信息ProcessInfo
 *
 * @param predicate ProcessInfo的筛选条件
 *
 * @return RunningProcessInfo 可能为空(Context为空或者不满足匹配条件时)
 */
private fun Context?.getMatchedAppProcessInfo(predicate: (ActivityManager.RunningAppProcessInfo) -> Boolean)
        : ActivityManager.RunningAppProcessInfo? {
    if (this == null) {
        return null
    }

    return when (val am = getSystemService(Context.ACTIVITY_SERVICE)) {
        is ActivityManager -> {
            return am.runningAppProcesses?.find { processInfo ->
                processInfo != null && predicate.invoke(
                    processInfo
                )
            }
        }
        else -> null
    }
}