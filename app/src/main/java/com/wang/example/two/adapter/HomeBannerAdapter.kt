package com.wang.example.two.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.wang.container.adapter.OneContainerItemAdapter
import com.wang.container.holder.BaseViewHolder
import com.wang.example.databinding.AdapterHomeBannerBinding
import com.wang.example.two.bean.HomeBannerBean

class HomeBannerAdapter : OneContainerItemAdapter<AdapterHomeBannerBinding, HomeBannerBean>() {

    override fun onBindChildViewHolder(
        holder: BaseViewHolder<AdapterHomeBannerBinding>,
        currentBean: HomeBannerBean
    ) {
        with(holder.vb) {
            tvTitle.text = currentBean.bannerInfo.title
            //随便写个rv
            rvBanner.adapter = object : RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): BaseViewHolder<ViewBinding> {
                    val iv = ImageView(parent.context)
                    iv.scaleType = ImageView.ScaleType.FIT_XY
                    iv.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    return BaseViewHolder(iv)
                }

                override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
                    val iv = holder.itemView as ImageView
                    iv.setImageResource(currentBean.bannerInfo.bannerResList[position])
                }

                override fun getItemCount(): Int {
                    return currentBean.bannerInfo.bannerResList.size
                }

            }
        }
    }

    override fun getSpanSize(currentBean: HomeBannerBean, relativePosition: Int): Int {
        return 4
    }
}