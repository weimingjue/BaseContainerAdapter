package com.wang.container.adapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.collection.ArraySet
import androidx.recyclerview.widget.RecyclerView
import com.wang.container.BaseContainerAdapter
import com.wang.container.R
import com.wang.container.bean.IContainerBean
import com.wang.container.holder.BaseViewHolder
import com.wang.container.interfaces.DefOnItemClickListener
import com.wang.container.interfaces.OnItemClickListener
import com.wang.container.observer.IContainerObserver

/**
 * 和普通adapter操作一样，加了个currentBean来确定当前adapter的数据
 * 所有的position均为相对position
 * 获取adapter在整个RecyclerView的绝对position见[getCurrentPositionInfo]或[BaseViewHolder.commonPosition]、[BaseViewHolder.listPosition]
 * 简单的只有一个条目见[OneContainerItemAdapter]
 */
abstract class BaseContainerItemAdapter<BEAN : IContainerBean> {
    private val observers = ArraySet<IContainerObserver>()
    private val wrapListener = MyItemClickListenerWrap()

    private var _containerAdapter: BaseContainerAdapter<*>? = null

    /**
     * observe主要用于notify
     * 此方法一般由父容器调用，所以不能加泛型
     */
    open fun registerDataSetObserver(observer: IContainerObserver) {
        observers.add(observer)
    }

    open fun unregisterDataSetObserver(observer: IContainerObserver) {
        observers.remove(observer)
    }

    /**
     * 没有position，不能使用getCurrent相关方法
     *
     * @param viewType 该adapter自己的type
     */
    fun createViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val holder = onCreateViewHolder(parent, viewType)
        holder.itemView.setTag(R.id.tag_view_holder, holder)
        holder.itemView.setTag(R.id.tag_view_adapter, this)
        holder.itemView.setTag(R.id.tag_view_container, _containerAdapter)
        return holder
    }

    /**
     * @param relativePosition 属于该adapter的position
     * 如：[getItemCount]=1(每个bean只对应一条数据)，这个position一直是0（就是没用的意思）
     * 如：[getItemCount]=xx(你的bean里面还有自己的list)，这个position就是相对的值
     */
    fun bindViewHolder(holder: BaseViewHolder<*>, currentBean: BEAN, relativePosition: Int) {
        holder.itemView.setTag(R.id.tag_view_bean, currentBean)
        holder.itemView.setOnClickListener(wrapListener)
        holder.itemView.setOnLongClickListener(wrapListener)
        onBindViewHolder(holder, currentBean, relativePosition)
    }

    private fun getSuggestDefClickListener() =
        wrapListener.listener as? DefOnItemClickListener<BEAN> ?: DefOnItemClickListener()

    /**
     * 建议使用[setOnItemClickListener]、[setOnItemLongClickListener]
     *
     * 自定义点击效果，包括点击长按等，需要熟悉[OnItemClickListener]类
     */
    open fun setOnItemClickListener(listener: OnItemClickListener<BEAN>?) {
        wrapListener.listener = listener
        notifyDataSetChanged()
    }

    open fun setOnItemClickListener(
        clickListener: ((
            view: View,
            relativePosition: Int,
            currentBean: BEAN,
            vh: BaseViewHolder<*>,
            itemAdapter: BaseContainerItemAdapter<*>,
            containerAdapter: BaseContainerAdapter<*>
        ) -> Unit)?
    ) {
        setOnItemClickListener(getSuggestDefClickListener().apply { onItemClick = clickListener })
    }

    open fun setOnItemLongClickListener(
        longClickListener: ((
            view: View,
            relativePosition: Int,
            currentBean: BEAN,
            vh: BaseViewHolder<*>,
            itemAdapter: BaseContainerItemAdapter<*>,
            containerAdapter: BaseContainerAdapter<*>
        ) -> Boolean)?
    ) {
        setOnItemClickListener(getSuggestDefClickListener().apply {
            onItemLongClick = longClickListener
        })
    }

    /**
     * adapter里的view设置tag后（使用[addItemViewClickWithTag]、[dispatchItemViewClickWithTag]）点击会回调此方法
     */
    open fun setOnItemViewClickListenerWithTag(
        clickListener: ((
            view: View,
            relativePosition: Int,
            currentBean: BEAN,
            vh: BaseViewHolder<*>,
            itemAdapter: BaseContainerItemAdapter<*>,
            containerAdapter: BaseContainerAdapter<*>,
            tag: String
        ) -> Unit)?
    ) {
        setOnItemClickListener(getSuggestDefClickListener().apply {
            onItemViewClickWithTag = clickListener
        })
    }

    /**
     * adapter里的view设置tag后（使用[addItemViewClickWithTag]、[dispatchItemViewLongClickWithTag]）长按会回调此方法
     */
    open fun setOnItemViewLongClickListenerWithTag(
        longClickListener: ((
            view: View,
            relativePosition: Int,
            currentBean: BEAN,
            vh: BaseViewHolder<*>,
            itemAdapter: BaseContainerItemAdapter<*>,
            containerAdapter: BaseContainerAdapter<*>,
            tag: String
        ) -> Boolean)?
    ) {
        setOnItemClickListener(getSuggestDefClickListener().apply {
            onItemViewLongClickWithTag = longClickListener
        })
    }

    open fun getOnItemClickListener(): OnItemClickListener<BEAN>? = wrapListener

    /**
     * 刷新全部的adapter数据,其他方法均是局部刷新
     */
    open fun notifyDataSetChanged() {
        observers.forEach { it.notifyDataSetChanged() }
    }

    /**
     * @param position 就是item的position（我自己会计算绝对位置）
     * @param bean     list的bean数据,没有bean的话无法确定位置
     */
    open fun notifyItemChanged(position: Int, bean: BEAN) {
        notifyItemChanged(position, 1, bean)
    }

    open fun notifyItemChanged(positionStart: Int, itemCount: Int, bean: BEAN) {
        observers.forEach { it.notifyItemChanged(positionStart, itemCount, bean) }
    }

    open fun notifyItemInserted(position: Int, bean: BEAN) {
        notifyItemInserted(position, 1, bean)
    }

    open fun notifyItemInserted(positionStart: Int, itemCount: Int, bean: BEAN) {
        observers.forEach { it.notifyItemInserted(positionStart, itemCount, bean) }
    }

    open fun notifyItemMoved(fromPosition: Int, toPosition: Int, bean: BEAN) {
        observers.forEach { it.notifyItemMoved(fromPosition, toPosition, bean) }
    }

    open fun notifyItemRemoved(position: Int, bean: BEAN) {
        notifyItemRemoved(position, 1, bean)
    }

    open fun notifyItemRemoved(positionStart: Int, itemCount: Int, bean: BEAN) {
        observers.forEach { it.notifyItemRemoved(positionStart, itemCount, bean) }
    }

    /**
     * 将容器自己传进来（会在[BaseContainerAdapter.addAdapter]立即调用,正常使用不会为null）
     */
    open fun attachContainer(containerAdapter: BaseContainerAdapter<*>) {
        _containerAdapter = containerAdapter
    }

    /**
     * [setOnItemClickListener]、[BaseContainerAdapter.setOnItemClickListener]这两个listener的事件分发
     */
    protected inner class MyItemClickListenerWrap : OnItemClickListener<BEAN> {
        /**
         * [setOnItemClickListener]
         */
        var listener: OnItemClickListener<BEAN>? = null

        override fun onClick(view: View) {
            observers.forEach { it.dispatchItemClicked(view) }
            listener?.onClick(view)
            super.onClick(view)
        }

        override fun onLongClick(view: View): Boolean {
            var state = false
            observers.forEach {
                state = state or it.dispatchItemLongClicked(view)
            }
            state = state or super.onLongClick(view)
            listener?.let {
                state = state or it.onLongClick(view)
            }
            return state
        }

        override fun onItemClick(view: View, relativePosition: Int) {
        }
    }

    protected fun setItemViewTag(view: View, holder: BaseViewHolder<*>, clickTag: String) {
        view.setTag(R.id.tag_view_holder, holder)
        view.setTag(R.id.tag_view_bean, holder.itemView.getTag(R.id.tag_view_bean))
        view.setTag(R.id.tag_view_adapter, this)
        view.setTag(R.id.tag_view_container, holder.itemView.getTag(R.id.tag_view_container))
        view.setTag(R.id.tag_view_adapter_item_view_tag, clickTag)
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 以下是经常用到或重写的方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 当前position在父adapter的附加信息
     *
     * 使用场景示例：有header展示线条，没header去掉线条；第一条展示红色，最后一条展示黑色
     */
    open fun getCurrentPositionInfo(
        bean: IContainerBean,
        itemAdapterPosition: Int
    ) = containerAdapter.getItemAdapterPositionInfo(bean, itemAdapterPosition)

    /**
     * 返回容器（会在[BaseContainerAdapter.addAdapter]立即调用,正常使用不会为null）
     */
    open val containerAdapter: BaseContainerAdapter<*>
        get() = _containerAdapter ?: throw NullPointerException("只有在addAdapter后才可调用")

    open fun getSpanSize(currentBean: BEAN, position: Int) = 1

    /**
     * @param position 相对的position
     * @return 不能超出范围, 超出就会被当成其他adapter的type(如果真的不够用可以自行下载修改min和max就行了)
     */
    @IntRange(
        from = BaseContainerAdapter.TYPE_MIN.toLong(),
        to = BaseContainerAdapter.TYPE_MAX.toLong()
    )
    open fun getItemViewType(currentBean: BEAN, position: Int) = 0

    abstract fun getItemCount(currentBean: BEAN): Int

    /**
     * @param viewType 该adapter自己的type
     */
    protected abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*>

    /**
     * 想取绝对position见[BaseContainerAdapter.getAbsPosition]或[BaseViewHolder.commonPosition]、[BaseViewHolder.listPosition]
     *
     * @param relativePosition 属于该adapter的position
     * 如：[getItemCount]=1(每个bean只对应一条数据)，这个position一直是0（就是没用的意思）
     * 如：[getItemCount]=xx(你的bean里面还有自己的list)，这个position就是相对的值
     */
    protected abstract fun onBindViewHolder(
        holder: BaseViewHolder<*>,
        currentBean: BEAN,
        relativePosition: Int,
    )

    /**
     * 给view设置点击事件到[setOnItemClickListener]中
     *
     * 点击回调见[setOnItemClickListener]、[OnItemClickListener]
     *
     * @param clickTag 由于id不便辨识和使用，在adapter中声明tag更便于查看和修改
     */
    protected fun addItemViewClickWithTag(view: View, holder: BaseViewHolder<*>, clickTag: String) {
        setItemViewTag(view, holder, clickTag)
        if (view !is RecyclerView) {//RecycleView暂不支持点击事件，如有自定义请直接调用下面2个方法
            view.setOnClickListener(getOnItemClickListener())
            view.setOnLongClickListener(getOnItemClickListener())
        }
    }

    /**
     * 直接调用里面view的点击
     */
    fun dispatchItemViewClickWithTag(view: View, holder: BaseViewHolder<*>, clickTag: String) {
        setItemViewTag(view, holder, clickTag)
        getOnItemClickListener()?.onClick(view)
    }

    /**
     * 直接调用里面view的长按
     */
    fun dispatchItemViewLongClickWithTag(view: View, holder: BaseViewHolder<*>, clickTag: String) {
        setItemViewTag(view, holder, clickTag)
        getOnItemClickListener()?.onLongClick(view)
    }
}