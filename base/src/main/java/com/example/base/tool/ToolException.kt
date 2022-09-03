/**
 * Copyright (c) 2022, Lollitech
 * All rights reserved
 * author: funaihui@lollitech.com
 * describe: 异常处理类
 **/
@file:JvmName("ToolException")

package com.lollypop.android.tool

import android.os.Looper
import java.io.PrintWriter
import java.io.StringWriter
import android.os.Handler


/**
 *
 * 是否开启debug模式。true：是。
 */
private var debugEnable = false

/**
 * log 日志消费器
 */
private var logConsumer: ((String, String, Throwable?) -> Unit)? = null

/**
 * 初始化数据
 *
 * @param isDebugEnable 开启debug模式，true：是 <br/>
 * @param consume   log消费器，第一个范性String -> tag; 第二个String -> error msg，Demo如下：<br/>
 *                      init(true, (tag, errorMsg) -> {
 *                          ToolLog.e(tag, errorMsg);
 *                      });
 */
fun init(isDebugEnable: Boolean, consume: (String, String, Throwable?) -> Unit) {
    debugEnable = isDebugEnable
    logConsumer = consume
}

/**
 * 错误打印。
 *
 * @param logTagCls      log tag class
 * @param customErrorMsg 自定义异常信息
 * @param e              异常Exception
 * @param isThrowException            debug模式下，是否崩溃，默认崩溃
 */
@JvmOverloads
fun printStackTrace(
    logTagCls: Class<*>?,
    customErrorMsg: String = "",
    e: Throwable?,
    isThrowException: Boolean = true
) {
    printStackTrace(logTagCls?.simpleName ?: "", customErrorMsg, e, isThrowException)
}

/**
 * 错误打印 (debug模式会主动抛出异常，挂断程序)
 *
 * @param logTag         log tag
 * @param customErrorMsg 自定义异常信息
 * @param e              异常Exception
 * @param isThrowException debug模式下，是否崩溃，默认崩溃
 */
@JvmOverloads
fun printStackTrace(logTag: String?, customErrorMsg: String = "", e: Throwable?, isThrowException: Boolean = true) {
    e?.printStackTrace()
    outputLog(
        logTag, "printStackTrace()-> " + ("Msg:" + customErrorMsg + ", Cause:" + getErrLogMsg(e)), e
    )
    throwExceptionOnUIThread("========== 发生异常，终断程序运行! ==========", e, isThrowException)
}

/**
 * 抛出异常，不同环境表现不同：
 * 1，debug环境：立即抛出异常。
 * 2，正式环境：[logConsumer] 行为而定。
 *
 * @param tag log tag
 * @param msg 错误信息
 * @param isThrowException debug模式下，是否崩溃，默认崩溃
 */
@JvmOverloads
fun throwException(tag: String? = "ToolException", msg: String?, isThrowException: Boolean = true) {
    outputLog(tag, msg)
    throwExceptionOnUIThread(msg, null, isThrowException)
}

/**
 *
 * 收集Throwable错误日志信息
 *
 * @param e Throwable
 * @return errorMsg
 */
fun getErrLogMsg(e: Throwable?): String {
    if (e == null) {
        return ""
    }

    val writer = StringWriter()
    val printWriter = PrintWriter(writer)
    e.printStackTrace(printWriter)
    var cause: Throwable? = e.cause

    while (cause != null) {
        cause.printStackTrace(printWriter)
        cause = cause.cause
    }

    val crashInfo = writer.toString()

    runCatching {
        printWriter.close()
        writer.close()
    }.onFailure { exception ->
        exception.printStackTrace()
    }

    return crashInfo
}

/**
 * 在主线程抛出异常(仅在debug模式下)
 *
 * @param msg 错误信息
 * @param isThrowException debug模式下，是否崩溃，默认崩溃
 */
fun throwExceptionOnUIThread(msg: String?, e: Throwable?, isThrowException: Boolean) {
    if (debugEnable && isThrowException) {
        if (isUIThread()) {
            throw CustomException("Msg:" + msg + ",\n Cause by:" + getErrLogMsg(e))
        } else {
            Handler(Looper.getMainLooper())
                .post {
                    throw CustomException("Msg:" + msg + ", \nCause by:" + getErrLogMsg(e))
                }
        }
    }
}

/**
 * log 输出
 */
fun outputLog(tag: String?, msg: String?, throwable: Throwable? = null) {
    logConsumer?.invoke(
        if (tag.isNullOrEmpty()) "ToolException" else tag,
        if (msg.isNullOrEmpty()) "" else msg,
        throwable
    )
}

class CustomException(exceptionMsg: String) : Throwable(exceptionMsg)