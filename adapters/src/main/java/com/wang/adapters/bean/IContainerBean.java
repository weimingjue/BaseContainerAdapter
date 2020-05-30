package com.wang.adapters.bean;

import com.wang.adapters.adapter.IContainerItemAdapter;

/**
 * 你的最外层bean必须继承该接口
 */
public interface IContainerBean {
    /**
     * 这个bean属于哪个adapter
     */
    Class<? extends IContainerItemAdapter> getItemAdapterClass();
}
