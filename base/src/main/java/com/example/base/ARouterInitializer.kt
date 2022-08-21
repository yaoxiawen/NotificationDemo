package com.example.base

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.alibaba.android.arouter.launcher.ARouter

class ARouterInitializer : Initializer<String> {
    override fun create(context: Context): String {
        if (BuildConfig.DEBUG) {    // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog()       // 打印日志
            ARouter.openDebug()     // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(context.applicationContext as Application)
        return "ARouterInit"
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}