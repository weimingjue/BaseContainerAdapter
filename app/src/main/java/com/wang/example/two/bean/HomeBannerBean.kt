package com.wang.example.two.bean

import com.google.gson.Gson
import com.wang.example.two.adapter.HomeBannerAdapter

class HomeBannerBean(oldBean: HomeBaseBean) : HomeBaseBean(oldBean) {
    val bannerInfo: BannerDataEntity = Gson().fromJson(oldBean.data, BannerDataEntity::class.java)

    data class BannerDataEntity(
        val title: String = "",
        val bannerResList: List<Int> = arrayListOf()
    )

    override fun getBindAdapterClass() = HomeBannerAdapter::class.java
}