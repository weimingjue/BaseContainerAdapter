package com.wang.container.interfaces

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes

/**
 * 所有list的adapter的接口
 */
interface IListAdapter<BEAN> : IAdapter {

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // list相关的方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val list: MutableList<BEAN>

    fun setListAndNotify(dataList: List<BEAN>?) {
        if (dataList !== list) { //同一个对象当然啥都不需要干了
            list.clear()
            if (dataList != null) {
                list.addAll(dataList)
            }
        }
        notifyDataSetChanged()
    }

    fun addAllListAndNotify(l: List<BEAN>?) {
        if (!l.isNullOrEmpty()) {
            list.addAll(l)
            notifyListItemRangeInserted(list.size, l.size)
        }
    }

    fun addDataAndNotify(bean: BEAN?) {
        if (bean != null) {
            notifyListItemInserted(list.size, bean)
        }
    }

    fun addFirstListAndNotify(l: List<BEAN>?) {
        if (!l.isNullOrEmpty()) {
            if (l.size == 1) {
                list.add(0, l[0])
            } else {
                val temp = l + list
                list.clear()
                list.addAll(temp)
            }
            notifyListItemRangeInserted(0, l.size)
        }
    }

    fun addFirstDataAndNotify(bean: BEAN?) {
        if (bean != null) {
            notifyListItemInserted(0, bean)
        }
    }

    /**
     * 获取指定bean
     *
     * @throws IndexOutOfBoundsException 不用多说吧
     */
    fun getItemData(listPosition: Int): BEAN {
        return list[listPosition]
    }

    fun getItemDataOrNull(listPosition: Int): BEAN? {
        return list.getOrNull(listPosition)
    }

    /**
     * 清空list,不刷新adapter
     */
    fun clearList() {
        list.clear()
    }

    /**
     * 添加全部条目,不刷新adapter，[addAllListAndNotify]
     */
    fun addAllList(addList: Collection<BEAN>?) {
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
    fun notifyListItemChanged(listPosition: Int, newBean: BEAN? = null) {
        if (listPosition < 0 || listPosition >= list.size) {
            return
        }
        if (newBean != null) {
            list[listPosition] = newBean
        }
        notifyItemChanged(listPosition + headerViewCount)
    }

    fun notifyListItemChanged(bean: BEAN) {
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
    fun notifyListItemInserted(listPosition: Int, insertBean: BEAN? = null) {
        if (listPosition < 0) {
            return
        }
        if (insertBean != null) {
            if (listPosition > list.size) {
                return
            }
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
        if (isRemoData) {
            if (listPosition >= list.size) {
                return
            }
            list.removeAt(listPosition)
        }
        notifyItemRemoved(listPosition + headerViewCount)
    }

    fun notifyListItemRangeRemoved(listPositionStart: Int, itemCount: Int) {
        notifyItemRangeRemoved(listPositionStart + headerViewCount, itemCount)
    }
}