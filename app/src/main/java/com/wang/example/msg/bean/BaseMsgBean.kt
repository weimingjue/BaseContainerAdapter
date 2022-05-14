package com.wang.example.msg.bean

import com.wang.container.adapter.BaseContainerItemAdapter
import com.wang.container.bean.IContainerBean
import com.wang.example.msg.adapter.UnsupportedAdapter

open class BaseMsgBean(oldBean: BaseMsgBean? = null) : IContainerBean {
    var msgType //1文字，2图片，3订单（data内有orderType：1未支付，2已支付，3新状态）
            = 0
    var data //type对应的数据
            : String? = null

    init {
        msgType = oldBean?.msgType ?: msgType
        data = oldBean?.data
    }


    override fun getBindAdapterClass(): Class<out BaseContainerItemAdapter<*>> =
        UnsupportedAdapter::class.java

    companion object {
        /**
         * 将bean改成对应的实现类
         */
        @JvmStatic
        fun formatListData(oldList: List<BaseMsgBean>): List<BaseMsgBean> {
            val list = ArrayList<BaseMsgBean>()
            for (bean in oldList) {
                val newBean = when (bean.msgType) {
                    1 -> TextBean(bean)
                    2 -> ImgBean(bean)
                    3 -> OrderBean(bean)
                    else -> BaseMsgBean(bean)
                }
                list.add(newBean)
            }
            return list
        }
    }
}