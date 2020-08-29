package com.wang.container.observer;

import android.view.View;

import androidx.annotation.NonNull;

import com.wang.container.bean.IContainerBean;

/**
 * observer
 * 暂时不加泛型：加泛型太繁琐，父容器register还容易出错
 */
public interface IContainerObserver {

    /**
     * 刷新全部的adapter数据,其他方法均是局部刷新
     */
    void notifyDataSetChanged();

    /**
     * @param position 就是item的position（我自己会计算绝对位置）
     * @param bean     list的bean数据,没有bean的话无法确定位置
     */
    @SuppressWarnings("unused")
    default void notifyItemChanged(int position, @NonNull IContainerBean bean) {
        notifyItemChanged(position, 1, bean);
    }

    void notifyItemChanged(int positionStart, int itemCount, @NonNull IContainerBean bean);

    @SuppressWarnings("unused")
    default void notifyItemInserted(int position, @NonNull IContainerBean bean) {
        notifyItemInserted(position, 1, bean);
    }

    void notifyItemInserted(int positionStart, int itemCount, @NonNull IContainerBean bean);

    void notifyItemMoved(int fromPosition, int toPosition, @NonNull IContainerBean bean);

    @SuppressWarnings("unused")
    default void notifyItemRemoved(int position, @NonNull IContainerBean bean) {
        notifyItemRemoved(position, 1, bean);
    }

    void notifyItemRemoved(int positionStart, int itemCount, @NonNull IContainerBean bean);

    /**
     * 当条目点击时调用
     */
    void dispatchItemClicked(View view);

    /**
     * @return 无论true、false所有的回调都会依次分发，有一个返回true长按事件就返回true
     */
    boolean dispatchItemLongClicked(View view);
}
