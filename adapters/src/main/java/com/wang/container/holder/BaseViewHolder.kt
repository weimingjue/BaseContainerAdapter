package com.wang.container.holder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.wang.container.R
import com.wang.container.interfaces.IListAdapter

/**
 * 所有ViewHolder的基类
 */
class BaseViewHolder<VB : ViewBinding>(item: View) : ViewHolder(item) {
    constructor(vb: VB) : this(vb.root) {
        _vb = vb
    }

    private var _vb: VB? = null

    /**
     * 调用时请确保用的是ViewBinding
     */
    val vb
        get() = _vb ?: throw IllegalArgumentException("没有传入vb，所以不能调用")

    val context: Context
        get() = itemView.context

    /**
     * 把adapterPosition和layoutPosition合并一起了
     */
    val commonPosition: Int
        get() {
            if (adapterPosition >= 0) {
                return adapterPosition
            }
            if (bindingAdapterPosition >= 0) {
                return bindingAdapterPosition
            }
            if (absoluteAdapterPosition >= 0) {
                return absoluteAdapterPosition
            }
            return layoutPosition
        }

    /**
     * 如果是listAdapter，则会减掉header
     */
    val listPosition: Int
        get() {
            var position = commonPosition
            var oa = itemView.getTag(R.id.tag_view_container)
            if (oa == null) {
                oa = itemView.getTag(R.id.tag_view_adapter)
            }
            if (oa is IListAdapter<*, *, *>) {
                if (oa.isHeaderView) {
                    position--
                }
            }
            return position
        }
}