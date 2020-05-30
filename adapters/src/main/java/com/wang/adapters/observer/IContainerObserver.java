package com.wang.adapters.observer;

import com.wang.adapters.bean.IContainerBean;

/**
 * observer
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
    void notifyItemChanged(int position, IContainerBean bean);

    void notifyItemChanged(int positionStart, int itemCount, IContainerBean bean);

    void notifyItemInserted(int position, IContainerBean bean);

    void notifyItemInserted(int positionStart, int itemCount, IContainerBean bean);

    void notifyItemMoved(int fromPosition, int toPosition, IContainerBean bean);

    void notifyItemRemoved(int position, IContainerBean bean);

    void notifyItemRemoved(int positionStart, int itemCount, IContainerBean bean);
}
