package com.example.notification

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.base.INotificationProvider
import java.util.concurrent.TimeUnit

class ReminderWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        const val TAG = "ReminderWorker"
    }

    override fun doWork(): Result {

        val reminderId =
            workerParams.inputData.getInt(ReminderWorkerManager.REMINDER_WORKER_DATA_ID, 0)
        val title =
            workerParams.inputData.getString(ReminderWorkerManager.REMINDER_WORKER_DATA_TITLE) ?: ""
        val content =
            workerParams.inputData.getString(ReminderWorkerManager.REMINDER_WORKER_DATA_CONTENT) ?: ""
        val link =
            workerParams.inputData.getString(ReminderWorkerManager.REMINDER_WORKER_DATA_LINK) ?: ""
        showNotification(title, content, link)
        //开始新一轮通知
        ReminderWorkerManager.sendWorkRequest(
            context,
            reminderId,
            title,
            content,
            link,
            System.currentTimeMillis() + 2 * 60 * 1000
        )

        return Result.success()
    }

    private fun showNotification(title: String, content: String, link: String) {
        val pending = INotificationProvider.generateDefaultActivityPendingIntent {
            "//route${link}"
        }
        INotificationProvider.configNotificationAndSend(title, content, pending)
    }
}

object ReminderWorkerManager {

    private const val TAG = "ReminderWorkerManager"
    const val REMINDER_WORKER_DATA_ID = "REMINDER_WORKER_DATA_ID"
    const val REMINDER_WORKER_DATA_TITLE = "REMINDER_WORKER_DATA_TITLE"
    const val REMINDER_WORKER_DATA_CONTENT = "REMINDER_WORKER_DATA_CONTENT"
    const val REMINDER_WORKER_DATA_LINK = "REMINDER_WORKER_DATA_LINK"

    fun sendWorkRequest(
        context: Context,
        reminderId: Int,
        title: String,
        content: String,
        link: String,
        triggerTime: Long
    ): OneTimeWorkRequest {
        val duration = triggerTime - System.currentTimeMillis()
        val data =
            Data.Builder().putInt(REMINDER_WORKER_DATA_ID, reminderId).putString(REMINDER_WORKER_DATA_TITLE, title)
                .putString(REMINDER_WORKER_DATA_CONTENT, content).putString(REMINDER_WORKER_DATA_LINK, link)
                .build()
        val uniqueWorkName =
            "reminderData_${reminderId}"
        val request = OneTimeWorkRequest.Builder(ReminderWorker::class.java)
            .setInitialDelay(duration, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(uniqueWorkName, ExistingWorkPolicy.REPLACE, request)
        return request
    }

    fun cancelWork(context: Context, reminderId: Int) {
        val uniqueWorkName =
            "reminderData_${reminderId}"
        WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
    }
}