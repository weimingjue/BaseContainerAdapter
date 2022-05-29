package com.wang.example.main.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewbinding.ViewBinding
import com.wang.container.adapter.OneContainerItemAdapter
import com.wang.container.holder.BaseViewHolder
import com.wang.container.interfaces.OnItemClickListener
import com.wang.example.main.bean.BaseMsgBean
import com.wang.example.utils.toast

class UnsupportedAdapter : OneContainerItemAdapter<ViewBinding, BaseMsgBean>() {
    override fun onCreateChildViewHolder(parent: ViewGroup): BaseViewHolder<ViewBinding> {
        val tv = AppCompatTextView(parent.context)
        tv.textSize = 15f
        tv.setPadding(0, 80, 0, 80)
        return BaseViewHolder(tv)
    }

    override fun onBindChildViewHolder(
        holder: BaseViewHolder<ViewBinding>,
        currentBean: BaseMsgBean
    ) {
        val tv: TextView = holder.itemView as TextView
        var text = "这是新版本的消息类型"
        val info = getCurrentPositionInfo(currentBean)
        if (info.isFirst) {
            text += "，整个列表第一个"
        }
        if (info.isLast) {
            text += "，整个列表最后一个"
        }
        tv.text = text
    }

    init {
        setOnItemClickListener(object : OnItemClickListener<BaseMsgBean> {
            override fun onItemClick(view: View, relativePosition: Int) {
                "您点击了新版本的新类型".toast()
            }

            override fun onItemLongClick(view: View, relativePosition: Int): Boolean {
                val currentBean = getCurrentBean(view)
                val vh = getViewHolder(view)
                val itemAdapter = getAdapter(view)
                val containerAdapter = getContainerAdapter(view)
                "长按新版本类型，自定义点击示例".toast()
                return true
            }
        })
    }
}