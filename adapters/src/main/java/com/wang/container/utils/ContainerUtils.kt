package com.wang.container.utils

import android.view.View
import android.widget.FrameLayout

internal object ContainerUtils {
    /**
     * 将fl的宽高和child同步
     */
    fun syncParamsToChild(
        fl: FrameLayout,
        childView: View
    ) {
        val flParams = fl.layoutParams
        val childParams = childView.layoutParams
        if (flParams != null && childParams != null) {
            flParams.width = childParams.width
            flParams.height = childParams.height
        }
    }
}