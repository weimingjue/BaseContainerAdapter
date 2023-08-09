package com.wang.container.interfaces

import android.view.View
import androidx.annotation.CallSuper
import com.sea.base.ext.view.adapterLayoutPosition
import com.sea.im.base.R
import com.wang.container.BaseContainerAdapter
import com.wang.container.adapter.BaseContainerItemAdapter
import com.wang.container.bean.IContainerBean

/**
 * 点击、长按的回调
 */
interface OnItemClickListener<BEAN : IContainerBean> : IItemClick {
    @CallSuper
    override fun onClick(view: View) {
        val tag = getViewClickTag(view)
        val position = getViewPosition(view)
        if (tag == null) {
            onItemClick(view, position)
        } else {
            onItemViewClickWithTag(view, position, tag)
        }
    }

    @CallSuper
    override fun onLongClick(view: View): Boolean {
        val tag = getViewClickTag(view)
        val position = getViewPosition(view)
        return if (tag == null) {
            onItemLongClick(view, position)
        } else {
            onItemViewLongClickWithTag(view, position, tag)
        }
    }

    /**
     * 返回相对的position
     */
    @CallSuper
    override fun getViewPosition(view: View): Int {
        val holder = getViewHolder(view)
        val absPosition = holder.adapterLayoutPosition
        val info = getContainerAdapter(view).getCacheItemPositionInfo(absPosition, true)
        return info.itemRelativePosition
    }

    /**
     * 获取container容器
     */
    @CallSuper
    fun getContainerAdapter(view: View): BaseContainerAdapter<*> {
        return view.getTag(R.id.tag_view_container) as BaseContainerAdapter<*>
    }

    /**
     * 获取当前view所保存的bean
     */
    @CallSuper
    fun getCurrentBean(view: View): BEAN {
        return view.getTag(R.id.tag_view_bean) as BEAN
    }

    /**
     * 获取当前view所在的adapter
     */
    @CallSuper
    fun getAdapter(view: View): BaseContainerItemAdapter<*> {
        return view.getTag(R.id.tag_view_adapter) as BaseContainerItemAdapter<*>
    }

    /**
     * item被点击时
     *
     * @param relativePosition 属于该adapter相对的position
     */
    override fun onItemClick(view: View, relativePosition: Int) {}

    /**
     * item被长按时
     */
    override fun onItemLongClick(view: View, relativePosition: Int) = false

    override fun onItemViewClickWithTag(view: View, relativePosition: Int, tag: String) {
    }

    override fun onItemViewLongClickWithTag(
        view: View,
        relativePosition: Int,
        tag: String
    ): Boolean {
        return false
    }
}