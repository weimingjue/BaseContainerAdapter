package com.wang.container.adapter

import android.view.ViewGroup
import androidx.annotation.IntRange
import com.wang.container.BaseContainerAdapter
import com.wang.container.bean.IContainerBean
import com.wang.container.bean.ItemAdapterPositionInfo
import com.wang.container.holder.BaseViewHolder
import com.wang.container.interfaces.IAdapter
import com.wang.container.interfaces.OnItemClickListener
import com.wang.container.observer.IContainerObserver

/**
 * adapter接口,见实现类[BaseContainerItemAdapter]
 *
 * 和普通adapter操作一样，加了个[getCurrentBean]来确定当前adapter的数据
 * 所有的position均为相对的position，获取adapter在整个RecyclerView的绝对position见[getCurrentPositionInfo]
 */
interface IContainerItemAdapter<BEAN : IContainerBean> : IAdapter<OnItemClickListener<BEAN>> {

    /**
     * observe主要用于notify
     * 此方法一般由父容器调用，所以不能加泛型
     */
    fun registerDataSetObserver(observer: IContainerObserver)
    fun unregisterDataSetObserver(observer: IContainerObserver)

    /**
     * 刷新全部的adapter数据,其他方法均是局部刷新
     */
    override fun notifyDataSetChanged()

    /**
     * @param position 就是item的position（我自己会计算绝对位置）
     * @param bean     list的bean数据,没有bean的话无法确定位置
     */
    fun notifyItemChanged(position: Int, bean: BEAN) {
        notifyItemChanged(position, 1, bean)
    }

    fun notifyItemChanged(positionStart: Int, itemCount: Int, bean: BEAN)
    fun notifyItemInserted(position: Int, bean: BEAN) {
        notifyItemInserted(position, 1, bean)
    }

    fun notifyItemInserted(positionStart: Int, itemCount: Int, bean: BEAN)
    fun notifyItemMoved(fromPosition: Int, toPosition: Int, bean: BEAN)
    fun notifyItemRemoved(position: Int, bean: BEAN) {
        notifyItemRemoved(position, 1, bean)
    }

    fun notifyItemRemoved(positionStart: Int, itemCount: Int, bean: BEAN)

    /**
     * 将容器自己传进来（会在[BaseContainerAdapter.addAdapter]立即调用,正常使用不会为null）
     */
    fun attachContainer(containerAdapter: BaseContainerAdapter<*>)

    /**
     * 没有position，不能使用getCurrent相关方法
     *
     * @param viewType 该adapter自己的type
     */
    override fun createViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*>

    /**
     * @param position 属于该adapter的position
     * 如：[.getItemCount]=1(每个bean只对应一条数据)，这个position一直是0（就是没用的意思）
     * 如：[.getItemCount]=xx(你的bean里面还有自己的list)，这个position就是相对的值
     */
    override fun bindViewHolder(holder: BaseViewHolder<*>, position: Int)

    fun getSpanSize(position: Int) = 1

    /**
     * @param position 相对的position
     * @return 不能超出范围, 超出就会被当成其他adapter的type(如果真的不够用可以自行下载修改min和max就行了)
     */
    @IntRange(
        from = BaseContainerAdapter.TYPE_MIN.toLong(),
        to = BaseContainerAdapter.TYPE_MAX.toLong()
    )
    override fun getItemViewType(position: Int) = 0

    /**
     * @throws ClassCastException 请检查你list里的bean对象和adapter的bean是否一致
     */
    @Deprecated("由container调用，不可自行调用")
    fun setCurrentBean(bean: BEAN?)

    /**
     * @return 当前的bean，每次想用的时候get就对了
     * 用的的地方：[getItemCount]、[bindViewHolder]、[getSpanSize]、[getItemViewType]...
     *
     * 注意：不能延时后调用，如onClickListener，请使用[OnItemClickListener.getCurrentBean]或get后声明为final
     * @throws NullPointerException 延时调用的才会抛出，为了便于检查错误
     */
    fun getCurrentBean(): BEAN

    /**
     * 当前position额外附加的数据，方便adapter使用
     * 子类能使用的地方[bindViewHolder]、[getItemViewType]、[getSpanSize]
     * 或者手动调用[BaseContainerAdapter.getItemAdapterPositionInfo]也行
     *
     * 使用场景：有header展示线条，没header去掉线条；第一条展示红色，最后一条展示黑色
     */
    fun getCurrentPositionInfo(): ItemAdapterPositionInfo

    @Deprecated("由container调用，不可自行调用")
    fun setCurrentPositionInfo(info: ItemAdapterPositionInfo)
}