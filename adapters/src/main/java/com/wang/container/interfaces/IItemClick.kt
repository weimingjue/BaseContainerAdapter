package com.wang.container.interfaces

import android.view.View
import android.view.View.OnLongClickListener
import androidx.annotation.CallSuper
import com.sea.base.adapter.BaseViewHolder
import com.sea.im.base.R

/**
 * OnItemClickListener的接口
 * 见子类实现[OnItemClickListener]
 */
interface IItemClick : View.OnClickListener, OnLongClickListener {

    /**
     * 获取当前view所在的position
     */
    fun getViewPosition(view: View): Int

    /**
     * 获取当前view所在的ViewHolder
     */
    @CallSuper
    fun getViewHolder(view: View): BaseViewHolder<*> {
        return view.getTag(R.id.tag_view_holder) as BaseViewHolder<*>
    }

    /**
     * 获取当前view点击时的tag
     */
    @CallSuper
    fun getViewClickTag(view: View): String? {
        return view.getTag(R.id.tag_view_adapter_item_view_tag) as? String
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 回调方法
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * item被点击时
     *
     * @param position 属于该adapter的position
     */
    fun onItemClick(view: View, position: Int)

    /**
     * item被长按时
     */
    fun onItemLongClick(view: View, position: Int): Boolean

    /**
     * item里的view被点击时
     *
     * @param position 属于该adapter的position
     */
    fun onItemViewClickWithTag(view: View, position: Int, tag: String)

    /**
     * item里的view被长按时
     */
    fun onItemViewLongClickWithTag(view: View, position: Int, tag: String): Boolean
}