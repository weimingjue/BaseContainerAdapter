package com.wang.container.interfaces

import android.view.ViewGroup
import com.wang.container.holder.BaseViewHolder

/**
 * 所有adapter的接口
 */
interface IAdapter {
    fun getItemCount(): Int
    fun getItemViewType(position: Int): Int
    fun createViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*>
    fun bindViewHolder(holder: BaseViewHolder<*>, position: Int)

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // notify相关方法
    /////////////////////////////////////////////////////////////////////////////////////////////////

    fun notifyDataSetChanged()

    fun notifyItemChanged(position: Int)

    fun notifyItemRangeChanged(positionStart: Int, itemCount: Int)

    fun notifyItemInserted(position: Int)

    fun notifyItemRangeInserted(positionStart: Int, itemCount: Int)

    fun notifyItemMoved(fromPosition: Int, toPosition: Int)

    fun notifyItemRemoved(position: Int)

    fun notifyItemRangeRemoved(positionStart: Int, itemCount: Int)
}