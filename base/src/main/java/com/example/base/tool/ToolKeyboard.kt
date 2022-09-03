/**
 * Copyright (c) 2022, Lollitech
 * All rights reserved
 * author: funaihui@lollitech.com
 * describe: 软键盘工具类 PS:修改方法时，请运行APP项目中的'TestToolKeyboradActivity'进行测试验证
 **/
@file:JvmName("ToolKeyboard")

package com.lollypop.android.tool

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout

/**
 * 打开软键盘
 *
 * @param context  上下文
 * @param editText 需要打开软键盘的编辑框
 */
fun showKeyboard(context: Context?, editText: EditText?) {
    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

    editText?.let {
        //当视图未获取焦点时，手动使其获取焦点
        if (!it.isFocused) {
            it.requestFocus()
        }
        imm?.showSoftInput(it, 0)
    }
}

/**
 * 关闭软键盘 <br/>
 * PS:若获取不到Activity的Context，请调用[.closeKeyboard]方法
 *
 * PS：此处的参数不能传入View.getContext()，在5.0以下的手机getContext方法获取的类型为TintContextWrapper
 * 不能转化为Activity类型，也不能传入ApplicationContext
 */
fun closeKeyboard(context: Context?) {
    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

    var view: View? = null
    if (context is Activity) {
        view = context.window.decorView
    }

    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
}

/**
 * 关闭软键盘
 *
 * @param view 视图，当前页面中任意一个视图
 */
fun closeKeyboard(view: View?) {
    val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * 判断软键盘是否显示，以200px为最小高度来判断键盘的高度
 *
 * @return true: 显示，反之则不显示或者传入的context为空
 */
fun Activity.isSoftInputVisible(): Boolean = isSoftInputVisible(200)

/**
 * 根据传入的键盘最小高度，判断软键盘是否显示
 *
 * @param minHeightOfSoftInput 键盘最小高度
 * @return true: 显示，反之则不显示或者传入的context为空
 */
fun Activity.isSoftInputVisible(minHeightOfSoftInput: Int): Boolean =
    getContentViewInvisibleHeight() >= minHeightOfSoftInput

/**
 * 获取页面中显示的第一个视图的高度 <br></br>
 * PS:目前只针对软键盘高度获取有效，其它用处未经测试，请慎用！
 *
 * @return 返回第一个可视视图的高度，如果异常情况（Context为空，或者Context不是Activity）
 */
fun Activity.getContentViewInvisibleHeight(): Int {
    val contentView = findViewById<FrameLayout>(android.R.id.content)
    val contentViewChild = contentView.getChildAt(0)
    val outRect = Rect()
    contentViewChild.getWindowVisibleDisplayFrame(outRect)
    return contentViewChild.bottom - outRect.bottom
}
