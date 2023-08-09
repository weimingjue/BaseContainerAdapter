package com.wang.container.utils

import com.wang.container.adapter.BaseContainerItemAdapter
import com.wang.container.bean.IContainerBean

/**
 * <*>调用方法时泛型居然是Nothing，实属醉了
 */
internal fun BaseContainerItemAdapter<*>.castSuperAdapter() =
    this as BaseContainerItemAdapter<IContainerBean>