package com.wang.example.two.bean

import com.wang.container.adapter.BaseContainerItemAdapter
import com.wang.container.bean.IContainerBean
import com.wang.example.two.adapter.HomeUnsupportedAdapter

/**
 * @param msgType 1轮播，2单行文字，3多列图片
 * @param data type对应的数
 */
open class HomeBaseBean(
    oldBean: HomeBaseBean? = null,
    val msgType: Int = oldBean?.msgType ?: 0,
    val data: String? = oldBean?.data
) :
    IContainerBean {

    override fun getBindAdapterClass(): Class<out BaseContainerItemAdapter<*>> =
        HomeUnsupportedAdapter::class.java

    companion object {
        /**
         * 将bean改成对应的实现类
         */
        @JvmStatic
        fun formatListData(oldList: List<HomeBaseBean>): List<HomeBaseBean> {
            val list = ArrayList<HomeBaseBean>()
            for (bean in oldList) {
                val newBean = when (bean.msgType) {
                    1 -> HomeBannerBean(bean)
                    2 -> HomeTextBean(bean)
                    3 -> HomeGoodsBean(bean)
                    else -> HomeBaseBean(bean)
                }
                list.add(newBean)
            }
            return list
        }
    }
}