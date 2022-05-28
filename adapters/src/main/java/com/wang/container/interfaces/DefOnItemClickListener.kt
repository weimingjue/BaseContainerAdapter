package com.wang.container.interfaces

import android.view.View
import com.wang.container.BaseContainerAdapter
import com.wang.container.adapter.BaseContainerItemAdapter
import com.wang.container.bean.IContainerBean
import com.wang.container.holder.BaseViewHolder

/**
 * 默认click、long click回调
 */
class DefOnItemClickListener<BEAN : IContainerBean> : OnItemClickListener<BEAN> {
    var onItemClick: ((
        view: View,
        relativePosition: Int,
        currentBean: BEAN,
        vh: BaseViewHolder<*>,
        itemAdapter: BaseContainerItemAdapter<*>,
        containerAdapter: BaseContainerAdapter<*>
    ) -> Unit)? = null
    var onItemLongClick: ((
        view: View,
        relativePosition: Int,
        currentBean: BEAN,
        vh: BaseViewHolder<*>,
        itemAdapter: BaseContainerItemAdapter<*>,
        containerAdapter: BaseContainerAdapter<*>
    ) -> Boolean)? = null

    override fun onItemClick(view: View, relativePosition: Int) {
        onItemClick?.invoke(
            view,
            relativePosition,
            getCurrentBean(view),
            getViewHolder(view),
            getAdapter(view),
            getContainerAdapter(view)
        )
    }

    override fun onItemLongClick(view: View, relativePosition: Int): Boolean {
        return onItemLongClick?.invoke(
            view,
            relativePosition,
            getCurrentBean(view),
            getViewHolder(view),
            getAdapter(view),
            getContainerAdapter(view)
        ) ?: false
    }
}