package com.wang.container.helper

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sea.base.adapter.BaseViewHolder
import com.sea.base.ext.view.MATCH_PARENT
import com.sea.base.ext.view.WRAP_CONTENT
import com.sea.base.ext.view.getDefLayoutParams
import com.wang.container.interfaces.IItemClick
import com.wang.container.interfaces.IListAdapter

open class BaseListAdapterHelper<BEAN>(
    adapter: IListAdapter<BEAN, *, *>,
    dataList: List<BEAN>?
) {
    val adapter = adapter as IListAdapter<BEAN, ViewBinding, IItemClick>
    val list: MutableList<BEAN> = if (dataList == null) ArrayList() else ArrayList(dataList)

    fun onCreateHeaderFooterViewHolder(parent: ViewGroup): BaseViewHolder<*> {
        val fl = FrameLayout(parent.context)
        fl.layoutParams = parent.getDefLayoutParams().apply { height = MATCH_PARENT }
        return BaseViewHolder<ViewBinding>(fl)
    }

    fun onBindHeaderFooterViewHolder(holder: BaseViewHolder<*>, headerOrFooterView: View) {
        val fl = holder.itemView as FrameLayout
        val oldParent = headerOrFooterView.parent as? ViewGroup
        if (oldParent != fl) {
            oldParent?.removeView(headerOrFooterView)
            fl.removeAllViews()
            syncParamsToChild(fl, headerOrFooterView)
            fl.addView(headerOrFooterView)
        }
    }

    /**
     * 将fl的宽高和child同步
     */
    private fun syncParamsToChild(
        fl: FrameLayout,
        childView: View
    ) {
        val flParams = fl.layoutParams
        val childParams = childView.layoutParams
        if (flParams != null && childParams != null) {
            flParams.width = childParams.width
            flParams.height = childParams.height
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