package com.wang.example.main.bean

import com.google.gson.Gson
import com.wang.example.main.adapter.TextAdapter

class TextBean(oldBean: BaseMsgBean) : BaseMsgBean(oldBean) {
    val textInfo: TextDataEntity = Gson().fromJson(oldBean.data, TextDataEntity::class.java)

    data class TextDataEntity(val text: String = "")

    override fun getBindAdapterClass() = TextAdapter::class.java
}