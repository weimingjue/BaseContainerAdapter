package com.wang.container

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.collection.SimpleArrayMap
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.wang.container.BaseContainerAdapter.Companion.TYPE_MAX
import com.wang.container.BaseContainerAdapter.Companion.TYPE_MIN
import com.wang.container.adapter.BaseContainerItemAdapter
import com.wang.container.bean.IContainerBean
import com.wang.container.bean.ItemAdapterPositionInfo
import com.wang.container.helper.BaseListAdapterHelper
import com.wang.container.holder.BaseViewHolder
import com.wang.container.interfaces.IListAdapter
import com.wang.container.interfaces.OnItemClickListener
import com.wang.container.observer.IContainerObserver
import com.wang.container.utils.castSuperAdapter

/**
 * 一个超级adapter可以添加其他adapter
 *
 *
 * 可以用在如：天猫首页、bilibili、今日头条、聊天列表页面
 *
 *
 * 核心思想：每个[BEAN]的item都当作一个adapter，为了复用和管理相同adapter，在子adapter传入了[BEAN]、[BaseContainerItemAdapter.getCurrentBean]
 *
 *
 * 使用前提（都是无关紧要的，但也要看看）：
 * 1.bean必须继承[IContainerBean]
 * 2.子adapter必须是[BaseContainerItemAdapter]、[BaseContainerItemAdapter]的子类
 * 3.子adapter的type必须在[TYPE_MAX]、[TYPE_MIN]之间
 * 4.如果是GridLayoutManager必须在adapter前设置（在rv.setAdapter或addAdapter之前或手动调用[changedLayoutManager]）
 * 5.有header时直接调用BaseContainerAdapter的[notifyItemChanged]相关方法时需要+1（所有adapter的通病，建议使用[notifyListItemChanged]）（子adapter不受此影响）
 * 其他限制暂时没发现
 *
 *
 * https://blog.csdn.net/weimingjue/article/details/106468916
 */
@SuppressLint("NotifyDataSetChanged")
open class BaseContainerAdapter<BEAN : IContainerBean> @JvmOverloads constructor(list: List<BEAN>? = null) :
    RecyclerView.Adapter<BaseViewHolder<*>>(),
    IListAdapter<BEAN, ViewBinding, OnItemClickListener<BEAN>> {

    companion object {
        const val TYPE_MAX = 100000
        const val TYPE_MIN = -100000
        private const val TYPE_MINUS = TYPE_MAX - TYPE_MIN
    }

    protected val TYPE_HEADER = -1 * TYPE_MINUS + 1
    protected val TYPE_FOOTER = -1 * TYPE_MINUS + 2 //防止和adapter重复
    private val itemPositionCacheInfo = ItemAdapterPositionInfo()
    private var onItemClickListener: OnItemClickListener<BEAN>? = null
    protected val adaptersManager = MyAdaptersManager()
    protected val observers: IContainerObserver = object : IContainerObserver {
        override fun notifyDataSetChanged() {
            this@BaseContainerAdapter.notifyDataSetChanged()
        }

        override fun notifyItemChanged(positionStart: Int, itemCount: Int, bean: IContainerBean) {
            val newPosition = getAbsPosition(bean, positionStart)
            this@BaseContainerAdapter.notifyItemRangeChanged(newPosition, itemCount, null)
        }

        override fun notifyItemInserted(positionStart: Int, itemCount: Int, bean: IContainerBean) {
            val newPosition = getAbsPosition(bean, positionStart)
            this@BaseContainerAdapter.notifyItemRangeInserted(newPosition, itemCount)
        }

        override fun notifyItemMoved(fromPosition: Int, toPosition: Int, bean: IContainerBean) {
            val newPosition = getAbsPosition(bean, fromPosition)
            this@BaseContainerAdapter.notifyItemMoved(
                newPosition,
                newPosition + (toPosition - fromPosition)
            )
        }

        override fun notifyItemRemoved(positionStart: Int, itemCount: Int, bean: IContainerBean) {
            val newPosition = getAbsPosition(bean, positionStart)
            this@BaseContainerAdapter.notifyItemRangeRemoved(newPosition, itemCount)
        }

        override fun dispatchItemClicked(view: View) {
            onItemClickListener?.onClick(view)
        }

        override fun dispatchItemLongClicked(view: View): Boolean {
            return onItemClickListener?.onLongClick(view) ?: false
        }
    }
    protected var recyclerView: RecyclerView? = null
    protected var layoutManager: GridLayoutManager? = null

    /**
     * list相关代码合并
     */
    private val listHelper = BaseListAdapterHelper(this, list)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_HEADER, TYPE_FOOTER -> {
                listHelper.onCreateHeaderFooterViewHolder(parent)
            }
            else -> {
                adaptersManager.getAdapter(viewType / TYPE_MINUS)
                    .createViewHolder(parent, viewType % TYPE_MINUS)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (getItemViewType(position)) {
            TYPE_HEADER -> {
                listHelper.onBindHeaderFooterViewHolder(holder, listHelper.headerView!!)
                return
            }
            TYPE_FOOTER -> {
                listHelper.onBindHeaderFooterViewHolder(holder, listHelper.footerView!!)
                return
            }
            else -> {
                val info = getItemPositionInfo(position)
                val itemAdapter = info.itemAdapter.castSuperAdapter()
                itemAdapter.currentBean = list[info._listPosition]
                itemAdapter.currentPositionInfo = info
                itemAdapter.bindViewHolder(holder, info.itemPosition)
                itemAdapter.currentBean = null
            }
        }
    }

    @Deprecated("暂时无法实现，请勿调用", ReplaceWith("无"))
    @TargetApi(999)
    override fun onCreateListViewHolder(parent: ViewGroup): BaseViewHolder<ViewBinding> {
        //这两个方法是给其他adapter用的：https://github.com/weimingjue/BaseAdapter
        throw RuntimeException("暂时无法实现，请勿调用")
    }

    @Deprecated("暂时无法实现，请勿调用", ReplaceWith("无"))
    @TargetApi(999)
    override fun onBindListViewHolder(
        holder: BaseViewHolder<ViewBinding>,
        listPosition: Int,
        bean: BEAN
    ) {
        throw RuntimeException("暂时无法实现，请勿调用")
    }

    override fun getItemViewType(position: Int): Int {
        if (isHeaderView && position == 0) {
            return TYPE_HEADER
        }
        if (isFooterView && itemCount == position + 1) {
            return TYPE_FOOTER
        }
        val info = getItemPositionInfo(position)
        val itemAdapter = info.itemAdapter.castSuperAdapter()
        itemAdapter.currentBean = list[info._listPosition]
        val itemType = itemAdapter.getItemViewType(info.itemPosition)
        itemAdapter.currentBean = null
        if (itemType < TYPE_MIN || itemType >= TYPE_MAX) {
            throw RuntimeException("你的adapter" + itemAdapter.javaClass + "的type必须在" + TYPE_MIN + "~" + TYPE_MAX + "之间，type：" + itemType)
        }
        //根据mItemAdapters的position返回type，取的时候比较方便
        return adaptersManager.getPosition(itemAdapter.javaClass) * TYPE_MINUS + itemType
    }

    override fun getItemCount(): Int {
        var count = 0
        if (isHeaderView) {
            count++
        }
        if (isFooterView) {
            count++
        }
        listHelper.list.forEach { bean ->
            val itemAdapter =
                adaptersManager.getAdapter(bean.getBindAdapterClass()).castSuperAdapter()
            itemAdapter.currentBean = bean
            count += itemAdapter.getItemCount()
            itemAdapter.currentBean = null
        }
        return count
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        checkLayoutManager()
    }

    protected fun checkLayoutManager() {
        (recyclerView?.layoutManager as? GridLayoutManager)?.let {
            if (layoutManager != it) {
                changedLayoutManager(it)
            }
        }
    }

    /**
     * 根据绝对position获取子adapter的相关信息
     *
     * @param absPosition 绝对position
     */
    protected fun getItemPositionInfo(absPosition: Int): ItemAdapterPositionInfo {
        var position = absPosition
        if (isHeaderView) {
            position-- //去掉header
        }
        //itemAdapter的position=0时的真实位置
        var itemStartPosition = 0
        listHelper.list.forEachIndexed { i, bean ->
            val itemAdapter =
                adaptersManager.getAdapter(bean.getBindAdapterClass()).castSuperAdapter()
            itemAdapter.currentBean = bean
            val itemCount = itemAdapter.getItemCount()
            itemAdapter.currentBean = null
            val nextStartPosition = itemStartPosition + itemCount
            //下一个adapter的位置比position大说明当前type就在这个adapter中
            if (nextStartPosition > position) {
                itemPositionCacheInfo._listPosition = i
                itemPositionCacheInfo._itemPosition = position - itemStartPosition

                //当前状态
                itemPositionCacheInfo._absState = 0
                if (position == 0) {
                    itemPositionCacheInfo._absState =
                        itemPositionCacheInfo.absState or ItemAdapterPositionInfo.ABS_STATE_FIRST_LIST_POSITION
                }
                if (position == listHelper.list.size - 1) {
                    itemPositionCacheInfo._absState =
                        itemPositionCacheInfo.absState or ItemAdapterPositionInfo.ABS_STATE_LAST_LIST_POSITION
                }
                if (isHeaderView) {
                    itemPositionCacheInfo._absState =
                        itemPositionCacheInfo.absState or ItemAdapterPositionInfo.ABS_STATE_HAS_HEADER
                }
                if (isFooterView) {
                    itemPositionCacheInfo._absState =
                        itemPositionCacheInfo.absState or ItemAdapterPositionInfo.ABS_STATE_HAS_FOOTER
                }
                //设置position信息
                itemAdapter.currentPositionInfo = itemPositionCacheInfo
                itemPositionCacheInfo._itemAdapter = itemAdapter
                return itemPositionCacheInfo
            } else {
                //循环相加
                itemStartPosition = nextStartPosition
            }
        }
        throw RuntimeException("没有取到对应的type,可能你没有(及时)刷新adapter")
    }

    /**
     * position、adapter、class唯一并且可以互相取值
     */
    protected inner class MyAdaptersManager {
        val map = SimpleArrayMap<Class<out BaseContainerItemAdapter<*>>, Int>(8)
        val list = ArrayList<BaseContainerItemAdapter<*>>(8)

        fun addAdapter(adapters: List<BaseContainerItemAdapter<*>>) {
            adapters.forEach { adapter ->
                adapter.attachContainer(this@BaseContainerAdapter)
                adapter.registerDataSetObserver(observers)
                if (!map.containsKey(adapter.javaClass)) {
                    list.add(adapter)
                    map.put(adapter.javaClass, list.lastIndex)
                }
            }
        }

        fun getAdapter(position: Int): BaseContainerItemAdapter<*> {
            return list.getOrNull(position)
                ?: throw RuntimeException("缺少对应的adapter，adapter数量：" + list.size + "，当前index：" + position)
        }

        fun getAdapter(cls: Class<out BaseContainerItemAdapter<*>?>): BaseContainerItemAdapter<*> {
            val index = map[cls] ?: throw RuntimeException("缺少对应的adapter：$cls")
            return list[index]
        }

        fun getPosition(cls: Class<out BaseContainerItemAdapter<*>>): Int {
            return map[cls] ?: throw NullPointerException("一般是数据变化没有(及时)刷新adapter导致的")
        }

        fun remove(position: Int) {
            val remove = list.removeAt(position)
            map.remove(remove.javaClass)
        }

        fun remove(cls: Class<out BaseContainerItemAdapter<*>>?) {
            map.remove(cls)?.let {
                list.removeAt(it)
            }
        }

        fun clear() {
            list.clear()
            map.clear()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 以下是可调用方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 添加adapter.重复则不会被添加,必须先删除
     * 当然可以预先添加用不到的adapter
     */
    fun addAdapter(vararg adapters: BaseContainerItemAdapter<*>) {
        addAdapter(adapters.toList())
    }

    /**
     * 添加adapter.重复则不会被添加,必须先删除
     * 当然可以预先添加用不到的adapter
     */
    fun addAdapter(adapters: List<BaseContainerItemAdapter<*>>) {
        adaptersManager.addAdapter(adapters)
        checkLayoutManager()
        notifyDataSetChanged()
    }

    /**
     * 删除指定adapter
     *
     * @param adapterPosition 按添加顺序第几个
     */
    fun removeAdapter(adapterPosition: Int) {
        adaptersManager.remove(adapterPosition)
        notifyDataSetChanged()
    }

    /**
     * 删除指定adapter
     *
     * @param adapterClass 哪个adapter
     */
    fun removeAdapter(adapterClass: Class<out BaseContainerItemAdapter<*>>?) {
        adaptersManager.remove(adapterClass)
        notifyDataSetChanged()
    }

    /**
     * 清空adapter
     */
    fun removeAllAdapter() {
        adaptersManager.clear()
        notifyDataSetChanged()
    }

    /**
     * 返回所有的adapter
     */
    fun getAdapters(): List<BaseContainerItemAdapter<*>> {
        return adaptersManager.list
    }

    override val list = listHelper.list

    /**
     * 这个回调和子adapter的事件回调都会被调用（这里先调，子adapter后调）
     *
     * 注意同样逻辑别写重复了
     * 如果没有收到回调请检查你的adapter有没有对[RecyclerView.ViewHolder.itemView]设置了点击事件
     *
     * position为相对position（绝对的没啥用处），想获得绝对位置[getAbsPosition]
     */
    override fun setOnItemClickListener(listener: OnItemClickListener<BEAN>?) {
        onItemClickListener = listener
    }

    override fun getOnItemClickListener() = onItemClickListener

    /**
     * 根据bean对象和adapter的相对位置获取绝对位置
     *
     * @param itemAdapterPosition 相对potion
     */
    fun getAbsPosition(bean: IContainerBean, itemAdapterPosition: Int): Int {
        var position = itemAdapterPosition
        listHelper.list.forEach { listBean ->
            if (listBean === bean) {
                if (isHeaderView) {
                    position++ //加上header
                }
                return position
            } else {
                val itemAdapter =
                    adaptersManager.getAdapter(bean.getBindAdapterClass()).castSuperAdapter()
                itemAdapter.currentBean = bean
                position += itemAdapter.getItemCount()
                itemAdapter.currentBean = null
            }
        }
        throw RuntimeException("在list中没有找到传入的bean对象$bean")
    }

    /**
     * 根据绝对position获取对应adapter的额外信息
     *
     * @return 这个对象是复用的，一次性消费，不要声明final或者保存
     */
    @MainThread
    fun getItemAdapterPositionInfo(absPosition: Int): ItemAdapterPositionInfo {
        return getItemPositionInfo(absPosition)
    }

    /**
     * 根据bean和相对position获取对应adapter的额外信息
     *
     * @param itemAdapterPosition 相对potion
     *
     * @return 这个对象是复用的，一次性消费，不要声明final或者保存
     */
    @MainThread
    fun getItemAdapterPositionInfo(
        bean: IContainerBean,
        itemAdapterPosition: Int
    ): ItemAdapterPositionInfo {
        return getItemAdapterPositionInfo(getAbsPosition(bean, itemAdapterPosition))
    }

    /**
     * 把rv的LayoutManager改成其他的GridLayoutManager时.此方法理论上没啥用
     */
    fun changedLayoutManager(manager: GridLayoutManager) {
        layoutManager = manager
        manager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (isHeaderView && position == 0) {
                    return manager.spanCount
                } else if (isFooterView && itemCount == position + 1) {
                    return manager.spanCount
                }
                val info = getItemPositionInfo(position)
                val itemAdapter = info.itemAdapter.castSuperAdapter()
                itemAdapter.currentBean = list[info._listPosition]
                val spanSize = itemAdapter.getSpanSize(info.itemPosition)
                itemAdapter.currentBean = null
                return spanSize
            }
        }
    }

    override var headerView: View?
        get() = listHelper.headerView
        set(value) {
            listHelper.headerView = value
        }
    override var footerView: View?
        get() = listHelper.footerView
        set(value) {
            listHelper.footerView = value
        }

    /**
     * 刷新list的position，解决[notifyItemChanged]的position问题
     */
    fun notifyListItemChanged(listPosition: Int) {
        notifyItemChanged(listPosition + headerViewCount)
    }
}