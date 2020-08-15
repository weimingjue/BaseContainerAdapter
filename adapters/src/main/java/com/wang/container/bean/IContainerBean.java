package com.wang.container.bean;

import androidx.annotation.NonNull;

import com.wang.container.adapter.IContainerItemAdapter;

/**
 * 你的最外层bean必须继承该接口
 */
public interface IContainerBean {
    /**
     * 这个bean属于哪个adapter
     */
    @NonNull
    Class<? extends IContainerItemAdapter> getBindAdapterClass();
}
