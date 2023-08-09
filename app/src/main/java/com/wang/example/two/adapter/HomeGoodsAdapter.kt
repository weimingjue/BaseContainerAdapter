package com.wang.example.two.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewbinding.ViewBinding
import com.wang.container.adapter.BaseContainerItemAdapter
import com.wang.container.holder.BaseViewHolder
import com.wang.example.databinding.AdapterHomeGoodsBinding
import com.wang.example.two.bean.HomeGoodsBean

class HomeGoodsAdapter : BaseContainerItemAdapter<HomeGoodsBean>() {
    private val typeHeader = 1
    private val typeBody = -1

    override fun getItemCount(currentBean: HomeGoodsBean): Int {
        return currentBean.goodsInfo.goodsResList.size + 1
    }

    override fun getItemViewType(currentBean: HomeGoodsBean, relativePosition: Int): Int {
        if (relativePosition == 0) {
            return typeHeader
        }
        return typeBody
    }

    override fun getSpanSize(currentBean: HomeGoodsBean, relativePosition: Int): Int {
        return when (getItemViewType(currentBean, relativePosition)) {
            typeHeader -> 4
            else -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        when (viewType) {
            typeHeader -> {
                val tv = AppCompatTextView(parent.context)
                tv.textSize = 15f
                tv.setPadding(0, 80, 0, 80)
                return BaseViewHolder<ViewBinding>(tv)
            }

            else -> {
                return BaseViewHolder(
                    AdapterHomeGoodsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<*>,
        currentBean: HomeGoodsBean,
        relativePosition: Int
    ) {
        when (getItemViewType(currentBean, relativePosition)) {
            typeHeader -> {
                val tv: TextView = holder.itemView as TextView
                var text = "这是商品标题：" + currentBean.goodsInfo.title
                val info = getCurrentPositionInfo(currentBean, relativePosition)
                if (info.isFirst) {
                    text += "，整个列表第一个"
                }
                if (info.isLast) {
                    text += "，整个列表最后一个"
                }
                tv.text = text
            }

            else -> {
                holder as BaseViewHolder<AdapterHomeGoodsBinding>
                val goodsIndex = relativePosition - 1
                holder.vb.ivGoods.setImageResource(currentBean.goodsInfo.goodsResList[goodsIndex])
            }
        }
    }
}