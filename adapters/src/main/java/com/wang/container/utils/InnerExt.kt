package com.wang.container.utils

import android.view.ViewGroup
import com.wang.container.adapter.BaseContainerItemAdapter
import com.wang.container.bean.IContainerBean

internal const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
internal const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT

/**
 * <*>调用方法时泛型居然是Nothing，实属醉了
 */
internal fun BaseContainerItemAdapter<*>.castSuperAdapter() =
    this as BaseContainerItemAdapter<IContainerBean>