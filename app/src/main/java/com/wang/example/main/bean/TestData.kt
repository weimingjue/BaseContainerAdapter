package com.wang.example.main.bean

import com.google.gson.Gson
import com.wang.example.R
import com.wang.example.two.bean.HomeBannerBean
import com.wang.example.two.bean.HomeBaseBean
import com.wang.example.two.bean.HomeGoodsBean
import com.wang.example.two.bean.HomeTextBean

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


    fun createHomeList(): List<HomeBaseBean> {
        val list = ArrayList<HomeBaseBean>()
        for (i in 1..19) {
            val data = when (i % 4) {
                0 -> createHomeBanner(i)
                1 -> createHomeText(i)
                2 -> createHomeGoods(i)
                else -> HomeBaseBean(msgType = 999)
            }
            list.add(data)
        }

        return list
    }

    private fun createHomeBanner(position: Int): HomeBaseBean {
        return HomeBaseBean(
            msgType = 1,
            data = Gson().toJson(
                HomeBannerBean.BannerDataEntity(
                    title = "假的轮播图$position",
                    bannerResList = listOf(R.drawable.ic_launcher_background, R.mipmap.ic_launcher)
                )
            )
        )
    }

    private fun createHomeText(position: Int): HomeBaseBean {
        return HomeBaseBean(
            msgType = 2,
            data = Gson().toJson(
                HomeTextBean.TextDataEntity(
                    listOf("第$position：0", "第$position：1", "第$position：2")
                )
            )
        )
    }

    private fun createHomeGoods(position: Int): HomeBaseBean {
        val goodsList = ArrayList<Int>()
        for (i in 1..19) {
            goodsList.add(R.drawable.ic_launcher_background)
        }
        return HomeBaseBean(
            msgType = 3,
            data = Gson().toJson(
                HomeGoodsBean.GoodsDataEntity(
                    title = "商品列表标题",
                    goodsResList = goodsList
                )
            )
        )
    }
}