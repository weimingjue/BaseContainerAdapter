package com.wang.example.two.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewbinding.ViewBinding
import com.wang.container.adapter.OneContainerItemAdapter
import com.wang.container.holder.BaseViewHolder
import com.wang.example.two.bean.HomeBaseBean

class HomeUnsupportedAdapter : OneContainerItemAdapter<ViewBinding, HomeBaseBean>() {
    override fun onCreateChildViewHolder(parent: ViewGroup): BaseViewHolder<ViewBinding> {
        val tv = AppCompatTextView(parent.context)
        tv.textSize = 15f
        tv.setPadding(0, 80, 0, 80)
        return BaseViewHolder(tv)
    }

    override fun onBindChildViewHolder(
        holder: BaseViewHolder<ViewBinding>,
        currentBean: HomeBaseBean
    ) {
        val tv: TextView = holder.itemView as TextView
        var text = "这是新版未知类型"
        val info = getCurrentPositionInfo(currentBean)
        if (info.isFirst) {
            text += "，整个列表第一个"
        }
        if (info.isLast) {
            text += "，整个列表最后一个"
        }
        tv.text = text
    }

    override fun getSpanSize(currentBean: HomeBaseBean, relativePosition: Int): Int {
        return 4
    }
}