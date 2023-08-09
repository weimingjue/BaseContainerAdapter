package com.wang.container.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.wang.container.interfaces.IListAdapter

/**
 * 两个position有点头疼，无从选择，合并成一个
 *
 * 注意点：
 * list里注意header、footer
 * 完全就没bind过，肯定还是-1了
 */
val RecyclerView.ViewHolder.adapterLayoutPosition: Int get() = if (layoutPosition < 0) bindingAdapterPosition else layoutPosition

/**
 * 获取list的真正position
 */
@JvmOverloads
fun RecyclerView.ViewHolder.getListPosition(adapter: RecyclerView.Adapter<*>? = bindingAdapter): Int {
    if (adapter is IListAdapter<*>) {
        return adapterLayoutPosition - adapter.headerViewCount
    }
    return -1
}

inline fun RecyclerView.ViewHolder.setOnClickListener(crossinline block: (View) -> Unit) {
    this.itemView.setOnClickListener { block.invoke(it) }
}

inline fun RecyclerView.ViewHolder.setOnLongClickListener(crossinline block: (View) -> Boolean) {
    this.itemView.setOnLongClickListener { block.invoke(it) }
}

@JvmOverloads
inline fun RecyclerView.ViewHolder.setOnFastClickListener(
    clickInterval: Long = 300,
    crossinline block: (View) -> Unit
) {
    var timestamp = System.currentTimeMillis()
    itemView.setOnClickListener {
        val interval = System.currentTimeMillis() - timestamp
        if (itemView.isClickable && interval >= clickInterval) {
            block(itemView)
        }
        timestamp = System.currentTimeMillis()
    }
}