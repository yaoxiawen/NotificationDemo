package com.example.base

import android.app.PendingIntent
import android.graphics.Bitmap
import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter

interface INotificationProvider : IProvider {

    fun generateDefaultBroadcastPendingIntent(linkParams: (() -> String)? = null): PendingIntent

    fun generateDefaultActivityPendingIntent(linkParams: (() -> String)): PendingIntent?

    fun configNotificationAndSend(
        title: String,
        content: String,
        pendingIntent: PendingIntent?,
        bitmap: Bitmap? = null
    )

    fun cancelAll()

    companion object {
        private fun getNotificationProvider(): INotificationProvider? {
            val provider = ARouter.getInstance().build(ARouterPath.NotificationProvider)
                .navigation()
            return provider as? INotificationProvider
        }

        fun generateDefaultBroadcastPendingIntent(linkParams: (() -> String)? = null): PendingIntent? {
            return getNotificationProvider()?.generateDefaultBroadcastPendingIntent(linkParams)
        }

        fun generateDefaultActivityPendingIntent(linkParams: (() -> String)): PendingIntent? {
            return getNotificationProvider()?.generateDefaultActivityPendingIntent(linkParams)
        }

        fun configNotificationAndSend(
            title: String,
            content: String,
            pendingIntent: PendingIntent? = null,
            bitmap: Bitmap? = null
        ) {
            getNotificationProvider()?.configNotificationAndSend(
                title,
                content,
                pendingIntent,
                bitmap
            )
        }

        fun cancelAll(){
            getNotificationProvider()?.cancelAll()
        }
    }
}