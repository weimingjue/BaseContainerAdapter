package com.wang.example.msg.adapter

import com.wang.container.adapter.OneContainerItemAdapter
import com.wang.container.holder.BaseViewHolder
import com.wang.example.R
import com.wang.example.databinding.AdapterMsgWaitPayOrderBinding
import com.wang.example.msg.bean.OrderBean
import com.wang.example.utils.toast

class WaitPayOrderAdapter : OneContainerItemAdapter<AdapterMsgWaitPayOrderBinding, OrderBean>() {
    companion object {
        const val TAG_CLICK_STATE = "tag_click_state"
    }

    override fun onBindChildViewHolder(
        holder: BaseViewHolder<AdapterMsgWaitPayOrderBinding>,
        bean: OrderBean
    ) {
        var text = "列表状态："
        val info = getCurrentPositionInfo(bean)
        if (info.isFirst) {
            text += "整个列表第一个"
        }
        if (info.isLast) {
            text += "整个列表最后一个"
        }
        if (info.isCenter) {
            text += "列表中间"
        }
        holder.vb.btState.text = text
        holder.vb.tvOrderNo.text = "订单号：${bean.orderInfo.orderNo}"
        holder.vb.tvOrderName.text = "订单名称：${bean.orderInfo.orderName}"
        setItemViewClickWithTag(holder.vb.btState, holder, TAG_CLICK_STATE)
    }

    init {
        setOnItemClickListener { view, _, _, vh, _, _ ->
            when (view.id) {
                R.id.bt_state -> "您点击了列表状态，绝对位置：${vh.commonPosition}".toast()
                else -> "您点击了待支付条目，绝对位置：${vh.commonPosition}".toast()
            }
        }
    }
}