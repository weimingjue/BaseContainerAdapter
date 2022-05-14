package com.wang.container.utils

import android.view.ViewGroup
import com.wang.container.adapter.IContainerItemAdapter
import com.wang.container.bean.IContainerBean

internal const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
internal const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT

internal fun IContainerItemAdapter<*>.castSuperAdapter() =
    this as IContainerItemAdapter<IContainerBean>