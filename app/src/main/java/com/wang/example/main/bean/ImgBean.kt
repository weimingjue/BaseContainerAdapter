package com.wang.example.main.bean

import com.google.gson.Gson
import com.wang.example.main.adapter.ImgAdapter

class ImgBean(oldBean: BaseMsgBean) : BaseMsgBean(oldBean) {
    val imgInfo: ImgDataEntity = Gson().fromJson(oldBean.data, ImgDataEntity::class.java)

    data class ImgDataEntity(val imgRes: Int = 0)

    override fun getBindAdapterClass() = ImgAdapter::class.java
}