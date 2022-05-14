package com.wang.example.msg.bean

import com.google.gson.Gson
import com.wang.example.msg.adapter.ImgAdapter

class ImgBean(oldBean: BaseMsgBean) : BaseMsgBean(oldBean) {
    var imgInfo: ImgDataEntity

    class ImgDataEntity {
        var imgRes = 0
    }

    override fun getBindAdapterClass() = ImgAdapter::class.java

    init {
        imgInfo = Gson().fromJson(oldBean.data, ImgDataEntity::class.java)
    }
}