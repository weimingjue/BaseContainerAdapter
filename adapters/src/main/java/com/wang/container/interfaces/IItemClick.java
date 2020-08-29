package com.wang.container.interfaces;

import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.wang.container.R;
import com.wang.container.holder.BaseViewHolder;

/**
 * OnItemClickListener的接口
 * 见子类实现{@link OnItemClickListener}
 */
public interface IItemClick extends View.OnClickListener, View.OnLongClickListener {

    @Override
    @CallSuper//一般不需要重写，所以加了此限制（如果真的不想调用super可以注解抑制掉错误）
    default void onClick(@NonNull View view) {
        onItemClick(view, getViewPosition(view));
    }

    @Override
    @CallSuper
    default boolean onLongClick(@NonNull View view) {
        return onItemLongClick(view, getViewPosition(view));
    }

    /**
     * 获取当前view所在的position
     */
    int getViewPosition(@NonNull View view);

    /**
     * 获取当前view所在的ViewHolder
     */
    @CallSuper
    default BaseViewHolder getViewHolder(@NonNull View view) {
        return (BaseViewHolder) view.getTag(R.id.tag_view_holder);
    }

    /**
     * 获取当前view所在的adapter
     */
    @CallSuper
    default IAdapter getAdapter(@NonNull View view) {
        return (IAdapter) view.getTag(R.id.tag_view_adapter);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 回调方法
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * item被点击时
     *
     * @param position 属于该adapter的position
     */
    void onItemClick(@NonNull View view, int position);

    /**
     * item被长按时
     */
    default boolean onItemLongClick(@NonNull View view, int position) {
        return false;
    }
}