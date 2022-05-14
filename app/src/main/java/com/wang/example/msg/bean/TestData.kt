package com.wang.example.msg.bean

import com.google.gson.Gson
import com.wang.example.R
import com.wang.example.msg.bean.BaseMsgBean

object TestData {
    /**
     * 生成测试数据
     */
    @JvmStatic
    fun createMsgList(): List<BaseMsgBean> {
        val list = ArrayList<BaseMsgBean>()
        for (i in 0..19) {
            val json = when (i % 5) {
                0 -> createText(i)
                1 -> createImg(i)
                2 -> createOrder(i, 1)
                3 -> createOrder(i, 2)
                else -> createOrder(i, 3)
            }
            list.add(Gson().fromJson(json, BaseMsgBean::class.java))
        }
        return list
    }

    private fun createText(position: Int): String {
        return "{\"msgType\":1,\"data\":\"{\\\"text\\\":\\\"$position\\\"}\"}"
    }

    private fun createImg(position: Int): String {
        return "{\"msgType\":2,\"data\":\"{\\\"imgRes\\\":" + R.mipmap.ic_launcher + "}\"}"
    }

    private fun createOrder(position: Int, orderType: Int): String {
        return "{\"msgType\":3,\"data\":\"{\\\"orderType\\\":$orderType,\\\"orderNo\\\":\\\"0000$position\\\",\\\"orderName\\\":\\\"哈哈\\\",\\\"otherOrderData\\\":{\\\"emsNo\\\":\\\"11\\\"}}\"}"
    }
}