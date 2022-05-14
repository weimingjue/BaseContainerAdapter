package com.wang.example.msg.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewbinding.ViewBinding
import com.wang.container.adapter.OneContainerItemAdapter
import com.wang.container.bean.ItemAdapterPositionInfo
import com.wang.container.holder.BaseViewHolder
import com.wang.container.interfaces.OnItemClickListener
import com.wang.example.msg.bean.TextBean
import com.wang.example.utils.toast

class TextAdapter : OneContainerItemAdapter<ViewBinding, TextBean>() {
    override fun onCreateChildViewHolder(parent: ViewGroup): BaseViewHolder<ViewBinding> {
        val tv = AppCompatTextView(parent.context)
        tv.textSize = 20f
        tv.setPadding(0, 80, 0, 80)
        return BaseViewHolder(tv)
    }

    override fun onBindChildViewHolder(
        holder: BaseViewHolder<ViewBinding>,
        bean: TextBean
    ) {
        val tv: TextView = holder.itemView as TextView
        var text = "这是文字：" + bean.textInfo.text
        val info: ItemAdapterPositionInfo = getCurrentPositionInfo()
        if (info.isFirst) {
            text += "，整个列表第一个"
        }
        if (info.isLast) {
            text += "，整个列表最后一个"
        }
        tv.text = text
    }

    init {
        setOnItemClickListener(object : OnItemClickListener<TextBean> {
            override fun onItemClick(view: View, relativePosition: Int) {
                val bean = getCurrentBean(view)
                "您点击了文字：${bean.textInfo.text}，绝对位置：${getViewHolder(view).commonPosition}".toast()
            }
        })
    }
}