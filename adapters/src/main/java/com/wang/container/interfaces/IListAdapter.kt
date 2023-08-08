package com.wang.container.interfaces

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.wang.container.holder.BaseViewHolder

/**
 * 所有list的adapter的接口
 */
interface IListAdapter<BEANS, DB : ViewBinding, LISTENER : IItemClick> : IAdapter<LISTENER> {

    /**
     * 支持手动设置FrameLayout.LayoutParams的属性：with、height、margin，不支持gravity
     * 如果没有Params则默认宽高为match、wrap
     */
    var headerView: View?
    var footerView: View?

    val headerViewCount get() = if (headerView == null) 0 else 1
    val isHeaderView get() = headerView != null
    val footerViewCount get() = if (footerView == null) 0 else 1
    val isFooterView get() = footerView != null

    fun removeHeaderView() {
        headerView = null
    }

    fun removeFooterView() {
        footerView = null
    }

    fun setHeaderView(context: Context, @LayoutRes layoutRes: Int) {
        if (layoutRes == 0) {
            removeHeaderView()
            return
        }
        headerView =
            LayoutInflater.from(context).inflate(layoutRes, FrameLayout(context), false)
    }

    fun setFooterView(context: Context, @LayoutRes layoutRes: Int) {
        if (layoutRes == 0) {
            removeFooterView()
            return
        }
        footerView =
            LayoutInflater.from(context).inflate(layoutRes, FrameLayout(context), false)
    }

    /**
     * 最终你的list的create
     *
     *
     * 默认用DataBinding create
     * 完全不需要的话覆盖整个方法就行了，不会出问题
     * 你也可以重写来添加自己的默认逻辑，如：全局隐藏显示、嵌套rv的默认属性设置等
     */
    fun onCreateListViewHolder(parent: ViewGroup): BaseViewHolder<DB>

    /**
     * 最终你的list的bind
     *
     * @param listPosition 已经做过处理,就是list的position
     */
    fun onBindListViewHolder(holder: BaseViewHolder<DB>, listPosition: Int, bean: BEANS)

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // list相关的方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val list: MutableList<BEANS>

    fun setListAndNotifyDataSetChanged(dataList: List<BEANS>?) {
        if (dataList !== list) { //同一个对象当然啥都不需要干了
            list.clear()
            if (dataList != null) {
                list.addAll(dataList)
            }
        }
        notifyDataSetChanged()
    }

    /**
     * 获取指定bean
     *
     * @throws IndexOutOfBoundsException 不用多说吧
     */
    fun getItemData(listPosition: Int): BEANS {
        return list[listPosition]
    }

    fun getItemDataOrNull(listPosition: Int): BEANS? {
        return list.getOrNull(listPosition)
    }

    /**
     * 清空list,不刷新adapter
     */
    fun clearList() {
        list.clear()
    }

    /**
     * 添加全部条目,不刷新adapter
     */
    fun addAllData(addList: Collection<BEANS>?) {
        if (addList != null && list !== addList) {
            list.addAll(addList)
        }
    }

    fun listSize(): Int {
        return list.size
    }

    /**
     * list是否为空
     */
    fun isEmptyList(): Boolean {
        return list.isEmpty()
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // notify相关方法
    /////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 刷新list的position，解决[notifyItemChanged]的position问题
     * @param newBean 新数据
     *                null：你已经自己更新过了，这里只需要调用更新数据
     *                notnull：更新该条数据
     */
    fun notifyListItemChanged(listPosition: Int, newBean: BEANS? = null) {
        if (listPosition < 0 || listPosition >= list.size) {
            return
        }
        if (newBean != null) {
            list[listPosition] = newBean
        }
        notifyItemChanged(listPosition + headerViewCount)
    }

    fun notifyListItemChanged(bean: BEANS) {
        notifyListItemChanged(list.indexOf(bean))
    }

    fun notifyListItemRangeChanged(listPositionStart: Int, itemCount: Int) {
        notifyItemRangeChanged(listPositionStart + headerViewCount, itemCount)
    }

    /**
     * @param insertBean 要插入的数据
     *                   null：你已经自己插入数据了，这里只需要调用更新数据
     *                   notnull：插入该条数据
     */
    fun notifyListItemInserted(listPosition: Int, insertBean: BEANS? = null) {
        if (insertBean != null) {
            list.add(listPosition, insertBean)
        }
        notifyItemInserted(listPosition + headerViewCount)
    }

    fun notifyListItemRangeInserted(listPositionStart: Int, itemCount: Int) {
        notifyItemRangeInserted(listPositionStart + headerViewCount, itemCount)
    }

    fun notifyListItemMoved(listFromPosition: Int, listToPosition: Int) {
        notifyItemMoved(listFromPosition + headerViewCount, listToPosition + headerViewCount)
    }

    /**
     * @param isRemoData 是否删除该数据
     *                false：你已经自己删除过了，这里只需要调用更新数据
     *                true：删除该条数据
     */
    fun notifyListItemRemoved(listPosition: Int, isRemoData: Boolean = false) {
        if (listPosition < 0) {
            return
        }
        if (isRemoData && listPosition < list.size) {
            list.removeAt(listPosition)
        }
        notifyItemRemoved(listPosition + headerViewCount)
    }

    fun notifyListItemRangeRemoved(listPositionStart: Int, itemCount: Int) {
        notifyItemRangeRemoved(listPositionStart + headerViewCount, itemCount)
    }
}