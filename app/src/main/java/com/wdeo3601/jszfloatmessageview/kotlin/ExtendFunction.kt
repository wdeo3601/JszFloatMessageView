package com.jiushizhuan.release.utils.kotlin

/**
 * Created by wendong on 2017/10/25 0025.
 * Email:       wdeo3601@163.com
 * Description: kotlin新特性（扩展函数），统一写在这里
 */

import android.view.View

/**
 * null对象调用 tostring() 方法时，输出 “”
 */
fun Any?.toString(): String {
    return this?.toString() ?: ""
}

fun View.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}
