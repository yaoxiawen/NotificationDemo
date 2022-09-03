package com.example.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.base.ARouterPath
import com.example.base.INotificationProvider

@Route(path = ARouterPath.NotificationProvider)
class NotificationProviderImpl : INotificationProvider {
    companion object {
        //渠道Id
        private const val CHANNEL_ID = "渠道Id"

        //渠道名
        private const val CHANNEL_NAME = "渠道名-简单通知"

        //渠道重要级
        private const val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT
    }

    private lateinit var context: Context

    //Notification的ID
    private var notifyId = 100
    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder

    override fun init(context: Context) {
        this.context = context
        //获取系统通知服务
        manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //创建通知渠道，Android8.0及以上需要
        createChannel()
        builder = NotificationCompat.Builder(context.applicationContext, CHANNEL_ID)
        initNotificationBuilder()
    }

    private fun createChannel() {
        //创建通知渠道，Android8.0及以上需要
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            CHANNEL_IMPORTANCE
        )
        manager.createNotificationChannel(notificationChannel)
    }

    /**
     * 初始化通知Builder的通用配置
     */
    private fun initNotificationBuilder() {
        builder
            .setAutoCancel(true) //设置这个标志当用户单击面板就可以让通知将自动取消
            .setSmallIcon(R.drawable.ic_reminder) //android 5.0做了限制，色彩丰富的图片也只能显示白色和透明两种颜色（这个icon只要背景需要透明，只让内容块纯白色）
            .setWhen(System.currentTimeMillis()) //通知产生的时间，会在通知信息里显示
            .setDefaults(Notification.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) //锁屏上显示通知的所有内容
            .setOngoing(false) //ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
        //8.0以下闪光灯配置
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setLights(-0xffff01, 300, 1000)
        }
    }

    override fun generateDefaultBroadcastPendingIntent(linkParams: (() -> String)?): PendingIntent {
        val intent = Intent(NotificationHandleReceiver.NOTIFICATION_HANDLE_ACTION)
        intent.setPackage(context.packageName)
        linkParams?.let {
            val params = it.invoke()
            intent.putExtra(NotificationHandleReceiver.NOTIFICATION_LINK, params)
        }
        return PendingIntent.getBroadcast(
            context,
            notifyId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    override fun generateDefaultActivityPendingIntent(linkParams: () -> String): PendingIntent? {
        val uri = Uri.parse(linkParams.invoke())
        val path = uri.path
        val names = uri.queryParameterNames
        if (path != null) {
            val activity = getActivityByARouter(path)
            val intent = Intent(context, activity)
            for (key in names) {
                intent.putExtra(key, uri.getQueryParameter(key))
            }
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        }
        return null
    }

    override fun configNotificationAndSend(title: String, content: String, pendingIntent: PendingIntent?,bitmap: Bitmap?) {
        builder.setContentTitle(title)
            .setContentText(content)
            .setWhen(System.currentTimeMillis())
            .setStyle(NotificationCompat.BigTextStyle().bigText(content)) //设置可以显示多行文本
//            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))//设置显示图片

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { //8.0以下系统,声音和震动在builder中设置
            val vibrateEnable: Boolean = true
            if (vibrateEnable) {
                //设置震动，用一个 long 的数组来表示震动状态，{1000,500，1000}表示的是先震动1秒、静止0.5秒、再震动1秒
                //在android4.4中兴手机上发现，震动模式只设置为{1000}，震动并没有生效，需要注意下；
                builder.setVibrate(longArrayOf(1000, 500, 1000))
            } else {
                builder.setVibrate(longArrayOf(0))
            }
//            val voiceEnable: Boolean = false
//            if (voiceEnable) { //自定义声音
//                builder.setSound(Uri.parse("android.resource://" + context.packageName + "/" + R.raw.warning))
//            } else {
//                builder.setSound(null)
//            }
        }
        builder.setContentIntent(pendingIntent)
        val notification = builder.build()
        notification.flags = Notification.FLAG_AUTO_CANCEL
        //发送通知
        manager.notify(notifyId, notification)
        //id自增
        notifyId++
    }
}