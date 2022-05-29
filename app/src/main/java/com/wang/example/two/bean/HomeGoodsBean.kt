package com.wang.example.two.bean

import com.google.gson.Gson
import com.wang.example.two.adapter.HomeGoodsAdapter

class HomeGoodsBean(oldBean: HomeBaseBean) : HomeBaseBean(oldBean) {
    val goodsInfo: GoodsDataEntity = Gson().fromJson(oldBean.data, GoodsDataEntity::class.java)

    data class GoodsDataEntity(val title: String = "", val goodsResList: List<Int> = arrayListOf())

    override fun getBindAdapterClass() = HomeGoodsAdapter::class.java
}