package com.wang.container.interfaces

import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.wang.container.R
import com.wang.container.holder.BaseViewHolder

/**
 * 所有adapter的接口
 */
interface IAdapter<LISTENER : IItemClick> {
    fun setOnItemClickListener(listener: LISTENER?)
    fun getOnItemClickListener(): LISTENER?
    fun getItemCount(): Int
    fun getItemViewType(position: Int) = 0
    fun createViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*>
    fun bindViewHolder(holder: BaseViewHolder<*>, position: Int)

    /**
     * 给view设置点击事件到[setOnItemClickListener]中
     * 也可自行设置点击事件，然后手动调用分发[dispatchItemViewClickWithTag]、[dispatchItemViewLongClickWithTag]
     *
     * 点击回调见[setOnItemClickListener]、setOnItemViewClickListenerWithTag、setOnItemViewLongClickListenerWithTag
     *
     * @param clickTag 由于id不便辨识和使用，在adapter中声明tag更便于查看和修改
     */
    @CallSuper
    fun addItemViewClickWithTag(view: View, holder: BaseViewHolder<*>, clickTag: String) {
        setItemViewTag(view, holder, clickTag)
        if (view !is RecyclerView) {//RecycleView暂不支持点击事件，如有自定义请直接调用下面2个方法
            view.setOnClickListener(getOnItemClickListener())
            view.setOnLongClickListener(getOnItemClickListener())
        }
    }

    /**
     * 直接调用里面view的点击
     */
    @CallSuper
    fun dispatchItemViewClickWithTag(view: View, holder: BaseViewHolder<*>, clickTag: String) {
        setItemViewTag(view, holder, clickTag)
        getOnItemClickListener()?.onClick(view)
    }

    /**
     * 直接调用里面view的长按
     */
    @CallSuper
    fun dispatchItemViewLongClickWithTag(view: View, holder: BaseViewHolder<*>, clickTag: String) {
        setItemViewTag(view, holder, clickTag)
        getOnItemClickListener()?.onLongClick(view)
    }

    @CallSuper
    fun setItemViewTag(view: View, holder: BaseViewHolder<*>, clickTag: String) {
        view.setTag(R.id.tag_view_holder, holder)
        view.setTag(R.id.tag_view_bean, holder.itemView.getTag(R.id.tag_view_bean))
        view.setTag(R.id.tag_view_adapter, this)
        view.setTag(R.id.tag_view_container, holder.itemView.getTag(R.id.tag_view_container))
        view.setTag(R.id.tag_view_adapter_item_view_tag, clickTag)
    }

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