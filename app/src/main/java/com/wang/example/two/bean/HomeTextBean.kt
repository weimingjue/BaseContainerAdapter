package com.wang.example.two.bean

import com.google.gson.Gson
import com.wang.example.two.adapter.HomeTextAdapter

class HomeTextBean(oldBean: HomeBaseBean) : HomeBaseBean(oldBean) {
    val textInfo: TextDataEntity = Gson().fromJson(oldBean.data, TextDataEntity::class.java)

    data class TextDataEntity(val textList: List<String> = arrayListOf())

    override fun getBindAdapterClass() = HomeTextAdapter::class.java
}