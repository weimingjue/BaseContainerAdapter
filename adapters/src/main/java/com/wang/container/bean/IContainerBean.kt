package com.wang.container.bean

import com.wang.container.adapter.IContainerItemAdapter

/**
 * 你的最外层bean必须继承该接口
 */
interface IContainerBean {
    /**
     * 这个bean属于哪个adapter
     */
    fun getBindAdapterClass(): Class<out IContainerItemAdapter<*>>
}