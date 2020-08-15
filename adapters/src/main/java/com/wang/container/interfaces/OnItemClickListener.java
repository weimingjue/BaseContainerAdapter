package com.wang.container.interfaces;

import android.view.View;

import androidx.annotation.NonNull;

import com.wang.container.bean.IContainerBean;
import com.wang.container.holder.BaseViewHolder;

/**
 * 点击,长按,header,footer的回调
 * 完美解决类似recyclerviewAdapter的setOnClickListener重复new对象的问题
 */
public abstract class OnItemClickListener<BEAN extends IContainerBean> implements IContainerItemClick<BEAN> {

    private BEAN mBean;
    private BaseViewHolder mViewHolder;

    @Override
    public final void setCurrentBean(@NonNull BEAN bean) {
        mBean = bean;
    }

    @Override
    public final void setCurrentViewHolder(@NonNull BaseViewHolder viewHolder) {
        mViewHolder = viewHolder;
    }

    @NonNull
    @Override
    public final BEAN getCurrentBean() {
        return mBean;
    }

    @Override
    public final BaseViewHolder getCurrentViewHolder() {
        return mViewHolder;
    }

    @Override
    public boolean onItemLongClick(@NonNull View view, int position) {
        return false;
    }
}
