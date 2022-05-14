package com.wang.example.msg.bean

import com.google.gson.Gson
import com.wang.example.msg.adapter.TextAdapter

class TextBean(oldBean: BaseMsgBean) : BaseMsgBean(oldBean) {
    var textInfo: TextDataEntity

    class TextDataEntity {
        var text: String? = null
    }

    override fun getBindAdapterClass() = TextAdapter::class.java

    init {
        textInfo = Gson().fromJson(oldBean.data, TextDataEntity::class.java)
    }
}