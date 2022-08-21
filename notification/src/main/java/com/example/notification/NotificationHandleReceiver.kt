package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationHandleReceiver : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_HANDLE_ACTION = "notification_handle_action"
        const val NOTIFICATION_LINK = "notificationLink"
        const val TAG = "NotificationReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == NOTIFICATION_HANDLE_ACTION) {
            val link = intent.getStringExtra(NOTIFICATION_LINK)
            Log.d(TAG, "notification link:$link")
            LinkManager.handleLink(link)
        }
    }
}