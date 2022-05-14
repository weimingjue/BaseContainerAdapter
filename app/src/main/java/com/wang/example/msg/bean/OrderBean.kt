package com.wang.example.msg.bean

import com.google.gson.Gson
import com.wang.container.adapter.BaseContainerItemAdapter
import com.wang.example.msg.adapter.PaySuccessOrderAdapter
import com.wang.example.msg.adapter.WaitPayOrderAdapter

class OrderBean(oldBean: BaseMsgBean) : BaseMsgBean(oldBean) {
    var orderInfo: OrderDataEntity

    data class OrderDataEntity(
        val orderType: Int = 0, //1未支付，2已支付
        val orderNo: String = "",
        val orderName: String = "",
        val otherOrderData: OtherOrderData = OtherOrderData()
    ) {
        data class OtherOrderData(val emsNo: String = "")
    }

    override fun getBindAdapterClass(): Class<out BaseContainerItemAdapter<*>> {
        when (orderInfo.orderType) {
            1 -> return WaitPayOrderAdapter::class.java
            2 -> return PaySuccessOrderAdapter::class.java
        }
        return super.getBindAdapterClass()
    }

    init {
        orderInfo = Gson().fromJson(oldBean.data, OrderDataEntity::class.java)
    }
}