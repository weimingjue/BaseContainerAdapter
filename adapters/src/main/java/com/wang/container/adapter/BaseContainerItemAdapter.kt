package com.wang.container.adapter

import android.view.View
import android.view.ViewGroup
import androidx.collection.ArraySet
import com.wang.container.BaseContainerAdapter
import com.wang.container.R
import com.wang.container.bean.IContainerBean
import com.wang.container.bean.ItemAdapterPositionInfo
import com.wang.container.holder.BaseViewHolder
import com.wang.container.interfaces.OnItemClickListener
import com.wang.container.observer.IContainerObserver

/**
 * [IContainerItemAdapter]的抽象类
 */
abstract class BaseContainerItemAdapter<BEAN : IContainerBean> : IContainerItemAdapter<BEAN> {
    private val observers = ArraySet<IContainerObserver>()
    private val wrapListener = MyItemClickListenerWrap()
    private var currentBean: BEAN? = null
    private var currentPositionInfo: ItemAdapterPositionInfo? = null
    private var _containerAdapter: BaseContainerAdapter<*>? = null

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 继承下来的基本实现,正常情况不需要再重写
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun registerDataSetObserver(observer: IContainerObserver) {
        observers.add(observer)
    }

    override fun unregisterDataSetObserver(observer: IContainerObserver) {
        observers.remove(observer)
    }

    final override fun createViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val holder = onCreateViewHolder(parent, viewType)
        holder.itemView.setTag(R.id.tag_view_holder, holder)
        holder.itemView.setTag(R.id.tag_view_adapter, this)
        holder.itemView.setTag(R.id.tag_view_container, _containerAdapter)
        return holder
    }

    final override fun bindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        holder.itemView.setTag(R.id.tag_view_bean, getCurrentBean())
        holder.itemView.setOnClickListener(wrapListener)
        holder.itemView.setOnLongClickListener(wrapListener)
        onBindViewHolder(holder, position)
    }

    override fun setOnItemClickListener(listener: OnItemClickListener<BEAN>?) {
        wrapListener.listener = listener
        notifyDataSetChanged()
    }

    @Deprecated("这个不是真是的listener", ReplaceWith("getRealItemClickListener"))
    override fun getOnItemClickListener(): OnItemClickListener<BEAN>? = wrapListener

    fun getRealItemClickListener(): OnItemClickListener<BEAN>? = wrapListener.listener

    override fun notifyDataSetChanged() {
        observers.forEach { it.notifyDataSetChanged() }
    }

    override fun notifyItemChanged(positionStart: Int, itemCount: Int, bean: BEAN) {
        observers.forEach { it.notifyItemChanged(positionStart, itemCount, bean) }
    }

    override fun notifyItemInserted(positionStart: Int, itemCount: Int, bean: BEAN) {
        observers.forEach { it.notifyItemInserted(positionStart, itemCount, bean) }
    }

    override fun notifyItemMoved(fromPosition: Int, toPosition: Int, bean: BEAN) {
        observers.forEach { it.notifyItemMoved(fromPosition, toPosition, bean) }
    }

    override fun notifyItemRemoved(positionStart: Int, itemCount: Int, bean: BEAN) {
        observers.forEach { it.notifyItemRemoved(positionStart, itemCount, bean) }
    }

    /**
     * @throws ClassCastException 请检查你list里的bean对象和adapter的bean是否一致
     */
    @Deprecated("由container调用，不可自行调用")
    override fun setCurrentBean(bean: BEAN?) {
        currentBean = bean
    }

    override fun attachContainer(containerAdapter: BaseContainerAdapter<*>) {
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
            super.onClick(view)
        }

        override fun onLongClick(view: View): Boolean {
            var state = false
            observers.forEach {
                state = state or it.dispatchItemLongClicked(view)
            }
            state = state or super.onLongClick(view)
            return state
        }

        override fun onItemClick(view: View, relativePosition: Int) {
            listener?.onItemClick(view, relativePosition)
        }

        override fun onItemLongClick(view: View, relativePosition: Int): Boolean {
            return listener?.onItemLongClick(view, relativePosition) ?: false
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 以下是经常用到或重写的方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @return 当前的bean，每次想用的时候get就对了
     * 用的的地方：[getItemCount]、[onBindViewHolder]、[getSpanSize]、[getItemViewType]...
     *
     * 注意：不能延时后调用，如onClickListener，请使用[OnItemClickListener.getCurrentBean]或get后声明为final
     */
    override fun getCurrentBean() = currentBean
        ?: throw NullPointerException("请注意调用时机，使用OnItemClickListener#getCurrentBean或get后声明为final")

    /**
     * 当前position额外附加的数据，方便adapter使用
     * 子类能使用的地方[bindViewHolder]、[getItemViewType]、[getSpanSize]
     * 或者手动调用[BaseContainerAdapter.getItemAdapterPositionInfo]也行
     *
     * 使用场景：有header展示线条，没header去掉线条；第一条展示红色，最后一条展示黑色
     */
    override fun getCurrentPositionInfo() = currentPositionInfo
        ?: throw NullPointerException("请注意调用时机，延迟请调用BaseContainerAdapter.getItemAdapterPositionInfo")

    @Deprecated("由container调用，不可自行调用")
    override fun setCurrentPositionInfo(info: ItemAdapterPositionInfo) {
        currentPositionInfo = info
    }

    /**
     * 返回容器（会在[BaseContainerAdapter.addAdapter]立即调用,正常使用不会为null）
     */
    val containerAdapter: BaseContainerAdapter<*>
        get() = _containerAdapter ?: throw NullPointerException("只有在addAdapter后才可调用")

    //    public int getItemCount() {}
    //    public int getSpanSize(int position) {}
    //    public int getItemViewType(int position) {}
    /**
     * @param viewType 该adapter自己的type
     */
    protected abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*>

    /**
     * 想取绝对position见[BaseContainerAdapter.getAbsPosition]
     *
     * @param position 属于该adapter的position
     * 如：[getItemCount]=1(每个bean只对应一条数据)，这个position一直是0（就是没用的意思）
     * 如：[getItemCount]=xx(你的bean里面还有自己的list)，这个position就是相对的值
     */
    protected abstract fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int)
}