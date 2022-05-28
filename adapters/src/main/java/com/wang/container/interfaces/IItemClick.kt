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
    fun onItemLongClick(view: View, position: Int): Boolean {
        return false
    }

    /**
     * item里的view被点击时
     *
     * @param position 属于该adapter的position
     */
    fun onItemViewClickWithTag(view: View, position: Int, tag: String) {
    }

    /**
     * item里的view被长按时
     */
    fun onItemViewLongClickWithTag(view: View, position: Int, tag: String): Boolean {
        return false
    }
}