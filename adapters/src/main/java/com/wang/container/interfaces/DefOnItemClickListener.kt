package com.wang.container.interfaces

import android.view.View
import com.sea.base.adapter.BaseViewHolder
import com.wang.container.BaseContainerAdapter
import com.wang.container.adapter.BaseContainerItemAdapter
import com.wang.container.bean.IContainerBean

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

    var onItemViewClickWithTag: ((
        view: View,
        relativePosition: Int,
        currentBean: BEAN,
        vh: BaseViewHolder<*>,
        itemAdapter: BaseContainerItemAdapter<*>,
        containerAdapter: BaseContainerAdapter<*>,
        tag: String
    ) -> Unit)? = null
    var onItemViewLongClickWithTag: ((
        view: View,
        relativePosition: Int,
        currentBean: BEAN,
        vh: BaseViewHolder<*>,
        itemAdapter: BaseContainerItemAdapter<*>,
        containerAdapter: BaseContainerAdapter<*>,
        tag: String
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

    override fun onItemViewClickWithTag(view: View, relativePosition: Int, tag: String) {
        super.onItemViewClickWithTag(view, relativePosition, tag)
        onItemViewClickWithTag?.invoke(
            view,
            relativePosition,
            getCurrentBean(view),
            getViewHolder(view),
            getAdapter(view),
            getContainerAdapter(view),
            tag
        )
    }

    override fun onItemViewLongClickWithTag(
        view: View,
        relativePosition: Int,
        tag: String
    ): Boolean {
        return onItemViewLongClickWithTag?.invoke(
            view,
            relativePosition,
            getCurrentBean(view),
            getViewHolder(view),
            getAdapter(view),
            getContainerAdapter(view),
            tag
        ) ?: false
    }
}