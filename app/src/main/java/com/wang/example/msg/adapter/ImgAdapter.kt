package com.wang.example.msg.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.wang.container.adapter.OneContainerItemAdapter
import com.wang.container.holder.BaseViewHolder
import com.wang.container.interfaces.OnItemClickListener
import com.wang.example.msg.bean.ImgBean
import com.wang.example.utils.toast

class ImgAdapter : OneContainerItemAdapter<ViewBinding, ImgBean>() {
    override fun onCreateChildViewHolder(parent: ViewGroup): BaseViewHolder<ViewBinding> {
        val iv = AppCompatImageView(parent.context)
        iv.layoutParams = RecyclerView.LayoutParams(300, 300)
        iv.setPadding(0, 80, 0, 80)
        return BaseViewHolder(iv)
    }

    override fun onBindChildViewHolder(
        holder: BaseViewHolder<ViewBinding>,
        bean: ImgBean
    ) {
        val iv = holder.itemView as ImageView
        iv.setImageResource(getCurrentBean().imgInfo.imgRes)
    }

    init {
        setOnItemClickListener(object : OnItemClickListener<ImgBean> {
            override fun onItemClick(view: View, relativePosition: Int) {
                "您点击了图片，绝对位置：${getViewHolder(view).commonPosition}".toast()
            }
        })
    }
}