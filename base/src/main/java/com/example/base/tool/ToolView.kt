/**
 * Copyright (c) 2022, Lollitech
 * All rights reserved
 * author: funaihui@lollitech.com
 * describe: 视图工具类
 **/
@file:JvmName("ToolView")

package com.lollypop.android.tool

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@IntDef(View.VISIBLE, View.GONE, View.INVISIBLE)
@Retention(RetentionPolicy.SOURCE)
annotation class ViewVisibility


/**
 * 显示或者隐藏 View视图
 * @param isVisible true:设置view为View.VISIBLE；false：设置view为View.GONE
 * @param views 视图集合
 */
fun toggle(isVisible: Boolean, vararg views: View?) {
    if (isVisible) {
        setVisibility(View.VISIBLE, *views)
    } else {
        setVisibility(View.GONE, *views)
    }
}

/**
 * 显示View视图
 *
 * @param views 视图集合
 */
fun showView(vararg views: View?) = setVisibility(View.VISIBLE, *views)

/**
 * 以GONE方式，隐藏View视图
 *
 * @param views 视图集合
 */
fun hideView(vararg views: View?) = setVisibility(View.GONE, *views)

/**
 * 设置View显示/隐藏
 *
 * @param visibility 值见:[View.VISIBLE]、[View.GONE]、[View.INVISIBLE]
 * @param views      视图集合
 */
fun setVisibility(@ViewVisibility visibility: Int, vararg views: View?) {
    if (views.isNotEmpty()) {
        views.forEach {
            it?.visibility = visibility
        }
    }
}

/**
 * 设置 View 显示/隐藏
 *
 * @param isVisible 值见: true:[View.VISIBLE]；false: [View.GONE]
 * @param views     视图集合
 */
fun setVisibility(isVisible: Boolean, vararg views: View?) {
    if (views.isNotEmpty()) {
        views.forEach {
            if (isVisible) {
                it?.visibility = View.VISIBLE
            } else {
                it?.visibility = View.GONE
            }
        }
    }
}

/**
 * 给TextView 设置
 *
 * @param tv         TextView
 * @param resIdOrTxt 值
 */
fun setText(tv: TextView?, resIdOrTxt: Any?) {
    when (resIdOrTxt) {
        is String -> tv?.text = resIdOrTxt
        is CharSequence -> tv?.text = resIdOrTxt
        is Int -> {
            try {
                tv?.setText(resIdOrTxt)
            } catch (e: Exception) {
                throwExceptionOnUIThread("setText() -> resIdOrTxt '$resIdOrTxt' has error!", e,true)
            }
        }
    }
}

/**
 * 设置多个文本颜色
 *
 * @param ctx       上下文
 * @param colorRes  色值资源，例如R.color.xx
 * @param textViews TextView集合
 */
fun setTextColor(ctx: Context?, @ColorRes colorRes: Int, vararg textViews: TextView?) {
    if (ctx == null || textViews.isEmpty()) {
        return
    }

    for (tv in textViews) {
        tv?.setTextColor(ContextCompat.getColor(ctx, colorRes))
    }
}

/**
 * 设置文本 为粗体样式 (Typeface.BOLD) <br/>
 * @param textViewArray  文本组件
 */
fun setTextBold(vararg textViewArray: TextView?) {
    textViewArray.let {
        textViewArray.forEach {
            it?.paint?.isFakeBoldText = true
            it?.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        }
    }
}

/**
 * 设置、取消 文本粗体样式 <br/>
 * @param isFakeBold 文本是否设置粗体 <br/>
 *      true：粗体(Typeface.BOLD)； <br/>
 *      false：非粗体(Typeface.NORMAL) <br/>
 *
 * @param textViewArray  文本组件
 */
fun setTextBold(isFakeBold: Boolean, vararg textViewArray: TextView?) {
    textViewArray.let {
        textViewArray.forEach {
            it?.paint?.isFakeBoldText = isFakeBold

            with(isFakeBold) {
                if (this) {
                    Typeface.BOLD
                } else {
                    Typeface.NORMAL
                }
            }.apply {
                it?.typeface = Typeface.defaultFromStyle(this)
            }
        }
    }
}

/**
 * 设置View的Tag
 *
 * @param view 视图
 * @param tag  Tag值
 */
fun setTag(view: View?, tag: Any?) {
    tag?.let { view?.tag = it }
}

/**
 * 以指定的key，设置View的Tag
 *
 * @param view   视图
 * @param tagKey Tag的Key值，注意要传入Ids资源文件中定义的id值，例如：R.id.x
 * @param tag    Tag值
 */
fun setTag(view: View?, @IdRes tagKey: Int, tag: Any?) {
    if (tagKey.ushr(24) >= 2) {
        tag?.let { view?.setTag(tagKey, it) }
    }
}

/**
 * 设置视图的背景图片
 *
 * @param view  视图
 * @param resId 背景图片资源，例如R.drawable.xx
 */
fun setBackgroundResource(view: View?, @DrawableRes resId: Int) = view?.setBackgroundResource(resId)

/**
 * 设置视图的背景色
 *
 * @param view     视图
 * @param colorRes 背景色值，例如R.color.xx
 */
fun setBackgroundColor(view: View?, @ColorRes colorRes: Int) =
    view?.setBackgroundColor(ContextCompat.getColor(view.context, colorRes))