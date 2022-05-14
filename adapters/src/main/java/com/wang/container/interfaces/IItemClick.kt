package com.wang.container.interfaces

import android.view.View
import android.view.View.OnLongClickListener
import androidx.annotation.CallSuper
import com.wang.container.R
import com.wang.container.holder.BaseViewHolder

/**
 * OnItemClickListener的接口
 * 见子类实现[OnItemClickListener]
 */
interface IItemClick : View.OnClickListener, OnLongClickListener {
    @CallSuper //一般不需要重写，所以加了此限制（如果真的不想调用super可以注解抑制掉错误）
    override fun onClick(view: View) {
        onItemClick(view, getViewPosition(view))
    }

    @CallSuper
    override fun onLongClick(view: View): Boolean {
        return onItemLongClick(view, getViewPosition(view))
    }

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
     * 获取当前view所在的adapter
     */
    @CallSuper
    fun getAdapter(view: View): IAdapter<*>? {
        return view.getTag(R.id.tag_view_adapter) as IAdapter<*>
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
    fun onItemLongClick(view: View, position: Int): Boolean {
        return false
    }
}