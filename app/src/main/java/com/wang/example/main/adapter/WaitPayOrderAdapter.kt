package com.wang.example.main.adapter

import com.wang.container.adapter.OneContainerItemAdapter
import com.wang.container.holder.BaseViewHolder
import com.wang.container.utils.adapterLayoutPosition
import com.wang.example.databinding.AdapterMsgWaitPayOrderBinding
import com.wang.example.main.bean.OrderBean
import com.wang.example.utils.toast

class WaitPayOrderAdapter : OneContainerItemAdapter<AdapterMsgWaitPayOrderBinding, OrderBean>() {

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
        holder.vb.btState.setOnClickListener {
            "您点击了列表按钮，绝对位置：${holder.adapterLayoutPosition}".toast()
        }
    }
}