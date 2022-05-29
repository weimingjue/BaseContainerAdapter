package com.wang.example.two.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewbinding.ViewBinding
import com.wang.container.adapter.BaseContainerItemAdapter
import com.wang.container.holder.BaseViewHolder
import com.wang.example.two.bean.HomeTextBean
import com.wang.example.utils.toast

class HomeTextAdapter : BaseContainerItemAdapter<HomeTextBean>() {

    override fun getItemCount(currentBean: HomeTextBean): Int {
        return currentBean.textInfo.textList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val tv = AppCompatTextView(parent.context)
        tv.textSize = 20f
        tv.setPadding(0, 80, 0, 80)
        tv.setBackgroundColor(0xffdddddd.toInt())
        return BaseViewHolder<ViewBinding>(tv)
    }

    override fun getSpanSize(currentBean: HomeTextBean, relativePosition: Int): Int {
        return 4
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<*>,
        currentBean: HomeTextBean,
        relativePosition: Int
    ) {
        val tv: TextView = holder.itemView as TextView

        var text = "这是文字：" + currentBean.textInfo.textList[relativePosition]
        val info = getCurrentPositionInfo(currentBean, relativePosition)
        if (info.isFirst) {
            text += "，整个列表第一个"
        }
        if (info.isLast) {
            text += "，整个列表最后一个"
        }
        tv.text = text
    }

    init {
        setOnItemClickListener { _, relativePosition, bean, vh, _, _ ->
            "您点击了文字：${bean.textInfo.textList[relativePosition]}，绝对位置：${vh.commonPosition}，相对位置：$relativePosition".toast()
        }
    }
}