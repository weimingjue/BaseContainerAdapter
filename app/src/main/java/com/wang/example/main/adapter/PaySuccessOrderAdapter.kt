package com.wang.example.main.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewbinding.ViewBinding
import com.wang.container.adapter.OneContainerItemAdapter
import com.wang.container.holder.BaseViewHolder
import com.wang.example.main.bean.OrderBean
import com.wang.example.utils.toast

class PaySuccessOrderAdapter : OneContainerItemAdapter<ViewBinding, OrderBean>() {
    override fun onCreateChildViewHolder(parent: ViewGroup): BaseViewHolder<ViewBinding> {
        val tv = AppCompatTextView(parent.context)
        tv.textSize = 20f
        tv.setPadding(0, 80, 0, 80)
        return BaseViewHolder(tv)
    }

    override fun onBindChildViewHolder(
        holder: BaseViewHolder<ViewBinding>,
        currentBean: OrderBean
    ) {
        val tv: TextView = holder.itemView as TextView
        var text =
            ("支付成功，订单号：" + currentBean.orderInfo.orderNo + "，订单名称" + currentBean.orderInfo.orderName
                    + "，物流：" + currentBean.orderInfo.otherOrderData.emsNo)
        val info = getCurrentPositionInfo(currentBean)
        if (info.isFirst) {
            text += "，整个列表第一个"
        }
        if (info.isLast) {
            text += "，整个列表最后一个"
        }
        tv.text = text
    }

    init {
        setOnItemClickListener { _, _, bean, _, _, _ ->
            "您点击了支付成功订单，订单号：${bean.orderInfo.orderNo}".toast()
        }
    }
}