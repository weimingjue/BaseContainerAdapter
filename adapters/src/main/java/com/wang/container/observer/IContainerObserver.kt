package com.wang.container.observer

import android.view.View
import com.wang.container.bean.IContainerBean

/**
 * observer
 * 暂时不加泛型：加泛型太繁琐，父容器register还容易出错
 */
interface IContainerObserver {
    /**
     * 刷新全部的adapter数据,其他方法均是局部刷新
     */
    fun notifyDataSetChanged()

    /**
     * @param position 就是item的position（我自己会计算绝对位置）
     * @param bean     list的bean数据,没有bean的话无法确定位置
     */
    fun notifyItemChanged(position: Int, bean: IContainerBean) {
        notifyItemChanged(position, 1, bean)
    }

    fun notifyItemChanged(positionStart: Int, itemCount: Int, bean: IContainerBean)
    fun notifyItemInserted(position: Int, bean: IContainerBean) {
        notifyItemInserted(position, 1, bean)
    }

    fun notifyItemInserted(positionStart: Int, itemCount: Int, bean: IContainerBean)
    fun notifyItemMoved(fromPosition: Int, toPosition: Int, bean: IContainerBean)
    fun notifyItemRemoved(position: Int, bean: IContainerBean) {
        notifyItemRemoved(position, 1, bean)
    }

    fun notifyItemRemoved(positionStart: Int, itemCount: Int, bean: IContainerBean)

    /**
     * 当条目点击时调用
     */
    fun dispatchItemClicked(view: View)

    /**
     * @return 无论true、false所有的回调都会依次分发，有一个返回true长按事件就返回true
     */
    fun dispatchItemLongClicked(view: View): Boolean
}