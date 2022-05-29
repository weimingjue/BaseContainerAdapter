package com.wang.example.main.adapter

import com.wang.container.adapter.OneContainerItemAdapter
import com.wang.container.holder.BaseViewHolder
import com.wang.example.databinding.AdapterMsgWaitPayOrderBinding
import com.wang.example.main.bean.OrderBean
import com.wang.example.utils.toast

class WaitPayOrderAdapter : OneContainerItemAdapter<AdapterMsgWaitPayOrderBinding, OrderBean>() {
    companion object {
        const val TAG_CLICK_STATE = "tag_click_state"
    }

    override fun onBindChildViewHolder(
        holder: BaseViewHolder<AdapterMsgWaitPayOrderBinding>,
        currentBean: OrderBean
    ) {
        var text = "列表状态："
        val info = getCurrentPositionInfo(currentBean)
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
        holder.vb.tvOrderNo.text = "订单号：${currentBean.orderInfo.orderNo}"
        holder.vb.tvOrderName.text = "订单名称：${currentBean.orderInfo.orderName}"
        addItemViewClickWithTag(holder.vb.btState, holder, TAG_CLICK_STATE)
    }

    init {
        setOnItemClickListener { _, _, _, vh, _, _ ->
            "您点击了待支付条目，绝对位置：${vh.commonPosition}".toast()
        }

        setOnItemViewClickListenerWithTag { _, _, _, vh, _, _, tag ->
            when (tag) {
                TAG_CLICK_STATE -> {
                    "您点击了列表按钮，绝对位置：${vh.commonPosition}".toast()
                }
            }
        }
    }
}