package com.wang.container.helper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.wang.container.databinding.CItemFlBinding
import com.wang.container.holder.BaseViewHolder
import com.wang.container.interfaces.IListAdapter
import com.wang.container.utils.ContainerUtils
import com.wang.container.utils.MATCH_PARENT
import com.wang.container.utils.WRAP_CONTENT

class BaseListAdapterHelper<BEAN>(val adapter: IListAdapter<*, *, *>, dataList: List<BEAN>?) {
    val list: MutableList<BEAN> = if (dataList == null) ArrayList() else ArrayList(dataList)

    fun onCreateHeaderFooterViewHolder(parent: ViewGroup): BaseViewHolder<*> {
        val flBinding = CItemFlBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder(flBinding)
    }

    fun onBindHeaderFooterViewHolder(holder: BaseViewHolder<*>, headerOrFooterView: View) {
        val fl = holder.itemView as FrameLayout
        val oldParent = headerOrFooterView.parent as? ViewGroup
        if (oldParent != fl) {
            oldParent?.removeView(headerOrFooterView)
            fl.removeAllViews()
            ContainerUtils.syncParamsToChild(fl, headerOrFooterView)
            fl.addView(headerOrFooterView)
        }
    }

    var headerView: View? = null
        set(value) {
            val oldHeaderView = field //旧view
            field = value
            if (field?.layoutParams == null) {
                field?.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            }

            //4种情况
            if (value == null && oldHeaderView != null) {
                if (adapter is RecyclerView.Adapter<*>) {
                    adapter.notifyItemRemoved(0)
                } else {
                    adapter.notifyDataSetChanged()
                }
            } else if (value != null && oldHeaderView == null) {
                if (adapter is RecyclerView.Adapter<*>) {
                    adapter.notifyItemInserted(0)
                } else {
                    adapter.notifyDataSetChanged()
                }
            } else if (value !== oldHeaderView) {
                if (adapter is RecyclerView.Adapter<*>) {
                    adapter.notifyItemChanged(0)
                } else {
                    adapter.notifyDataSetChanged()
                }
            } //else相等忽略
        }
    var footerView: View? = null
        set(value) {
            val oldFooterView = field //旧view
            field = value
            if (field?.layoutParams == null) {
                field?.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            }

            //4种情况
            if (value == null && oldFooterView != null) {
                if (adapter is RecyclerView.Adapter<*>) {
                    adapter.notifyItemRemoved(adapter.getItemCount()) //count已经减一了，所以不用减了
                } else {
                    adapter.notifyDataSetChanged()
                }
            } else if (value != null && oldFooterView == null) {
                if (adapter is RecyclerView.Adapter<*>) {
                    adapter.notifyItemInserted(adapter.getItemCount() - 1) //count已经加一了，所以需要减掉
                } else {
                    adapter.notifyDataSetChanged()
                }
            } else if (value !== oldFooterView) {
                if (adapter is RecyclerView.Adapter<*>) {
                    adapter.notifyItemChanged(adapter.getItemCount() - 1) //count不变
                } else {
                    adapter.notifyDataSetChanged()
                }
            } //else相等忽略
        }

}