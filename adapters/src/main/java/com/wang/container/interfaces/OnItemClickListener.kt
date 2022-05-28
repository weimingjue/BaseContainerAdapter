package com.wang.container.interfaces

import android.view.View
import androidx.annotation.CallSuper
import com.wang.container.BaseContainerAdapter
import com.wang.container.R
import com.wang.container.adapter.BaseContainerItemAdapter
import com.wang.container.bean.IContainerBean

/**
 * 点击、长按、header、footer的回调
 */
interface OnItemClickListener<BEAN : IContainerBean> : IItemClick {
    /**
     * 返回相对的position
     */
    @CallSuper
    override fun getViewPosition(view: View): Int {
        val holder = getViewHolder(view)
        val absPosition = holder.commonPosition
        val info = getContainerAdapter(view).getItemAdapterPositionInfo(absPosition)
        return info.itemPosition
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
    override fun onItemClick(view: View, relativePosition: Int)

    /**
     * item被长按时
     */
    override fun onItemLongClick(view: View, relativePosition: Int): Boolean {
        return false
    }
}