package com.example.notificationdemo

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.example.base.ARouterPath
import com.example.base.INotificationProvider
import com.example.notification.ReminderWorkerManager
import com.example.notificationdemo.databinding.ActivityMainBinding

@Route(path = ARouterPath.MainActivity)
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    //开启横幅通知返回
    private val bannerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(binding.root)
        binding.jump.setOnClickListener {
            ARouter.getInstance().build(ARouterPath.SecondActivity).navigation()
        }

        binding.btn.setOnClickListener {
            //由于Android12及更高版本的限制，当用户点按通知或通知中的操作按钮时，您的应用无法在服务或广播接收器内调用 startActivity()。
            //can't start activities from services or broadcast receivers that are used as notification trampolines
            //所以使用PendingIntent.getActivity来产生可以跳转页面的 PendingIntent
            val pending = INotificationProvider.generateDefaultActivityPendingIntent {
                "//route${ARouterPath.SecondActivity}"
            }
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.cat)
            INotificationProvider.configNotificationAndSend("yxw", "这是内容", pending, bitmap)
        }

        binding.btnDelay.setOnClickListener {
            ReminderWorkerManager.sendWorkRequest(
                this,
                0,
                "yxw",
                "这是延迟的内容",
                ARouterPath.SecondActivity,
                System.currentTimeMillis() + 2 * 60 * 1000
            )
        }

        binding.btnBanner.setOnClickListener {
//            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
//                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
//                .putExtra(Settings.EXTRA_CHANNEL_ID, "banner")
//            bannerLauncher.launch(intent)
        }
    }
}