package com.wang.container.interfaces;

import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.wang.container.BaseContainerAdapter;
import com.wang.container.R;
import com.wang.container.bean.IContainerBean;
import com.wang.container.bean.ItemAdapterPositionInfo;
import com.wang.container.holder.BaseViewHolder;

/**
 * 点击,长按,header,footer的回调
 * 完美解决类似recyclerviewAdapter的setOnClickListener重复new对象的问题
 */
public interface OnItemClickListener<BEAN extends IContainerBean> extends IItemClick {

    /**
     * 返回相对的position
     */
    @Override
    @CallSuper
    default int getViewPosition(@NonNull View view) {
        BaseViewHolder holder = getViewHolder(view);
        int absPosition = holder.getCommonPosition();
        ItemAdapterPositionInfo info = getContainerAdapter(view).getItemAdapterPositionInfo(absPosition);
        return info.mItemPosition;
    }

    /**
     * 获取container容器
     */
    @CallSuper
    default BaseContainerAdapter getContainerAdapter(@NonNull View view) {
        return (BaseContainerAdapter) view.getTag(R.id.tag_view_container);
    }

    /**
     * 获取当前view所保存的bean
     */
    @CallSuper
    default BEAN getCurrentBean(@NonNull View view) {
        //noinspection unchecked
        return (BEAN) view.getTag(R.id.tag_view_bean);
    }

    /**
     * item被点击时
     *
     * @param relativePosition 属于该adapter相对的position
     */
    @Override
    void onItemClick(@NonNull View view, int relativePosition);

    /**
     * item被长按时
     */
    @Override
    default boolean onItemLongClick(@NonNull View view, int relativePosition) {
        return false;
    }
}
