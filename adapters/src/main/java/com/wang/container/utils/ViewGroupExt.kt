package com.wang.container.utils

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

private val dlpMethod = run {
    val method = ViewGroup::class.java.getDeclaredMethod("generateDefaultLayoutParams")
    method.isAccessible = true
    return@run method
}

/**
 * [ViewGroup.generateDefaultLayoutParams]是protected的，所以有此拓展
 */
fun ViewGroup.getDefLayoutParams(): ViewGroup.LayoutParams {
    return if (this is RecyclerView) {
        this.layoutManager?.generateDefaultLayoutParams() ?: RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    } else {
        dlpMethod.invoke(this) as ViewGroup.LayoutParams
    }
}