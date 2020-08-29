package com.wang.container.adapter;

import android.view.ViewGroup;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.wang.container.BaseContainerAdapter;
import com.wang.container.bean.IContainerBean;
import com.wang.container.bean.ItemAdapterPositionInfo;
import com.wang.container.holder.BaseViewHolder;
import com.wang.container.interfaces.IAdapter;
import com.wang.container.interfaces.OnItemClickListener;
import com.wang.container.observer.IContainerObserver;

/**
 * adapter接口,见实现类{@link BaseContainerItemAdapter}
 * <p>
 * 和普通adapter操作一样，加了个{@link #getCurrentBean}来确定当前adapter的数据
 * 所有的position均为相对的position，获取adapter在整个RecyclerView的绝对position见{@link #getCurrentPositionInfo()}
 */
public interface IContainerItemAdapter<BEAN extends IContainerBean> extends IAdapter<OnItemClickListener<BEAN>> {

    /**
     * observe主要用于notify
     * 此方法一般由父容器调用，所以不能加泛型
     */
    void registerDataSetObserver(@NonNull IContainerObserver observer);

    @SuppressWarnings("unused")
    void unregisterDataSetObserver(@NonNull IContainerObserver observer);

    /**
     * 刷新全部的adapter数据,其他方法均是局部刷新
     */
    void notifyDataSetChanged();

    /**
     * @param position 就是item的position（我自己会计算绝对位置）
     * @param bean     list的bean数据,没有bean的话无法确定位置
     */
    @SuppressWarnings("unused")
    default void notifyItemChanged(int position, @NonNull BEAN bean) {
        notifyItemChanged(position, 1, bean);
    }

    void notifyItemChanged(int positionStart, int itemCount, @NonNull BEAN bean);

    @SuppressWarnings("unused")
    default void notifyItemInserted(int position, @NonNull BEAN bean) {
        notifyItemInserted(position, 1, bean);
    }

    void notifyItemInserted(int positionStart, int itemCount, @NonNull BEAN bean);

    @SuppressWarnings("unused")
    void notifyItemMoved(int fromPosition, int toPosition, @NonNull BEAN bean);

    @SuppressWarnings("unused")
    default void notifyItemRemoved(int position, @NonNull BEAN bean) {
        notifyItemRemoved(position, 1, bean);
    }

    void notifyItemRemoved(int positionStart, int itemCount, @NonNull BEAN bean);

    /**
     * 将容器自己传进来（会在{@link BaseContainerAdapter#addAdapter}立即调用,正常使用不会为null）
     */
    void attachContainer(@NonNull BaseContainerAdapter containerAdapter);

    /**
     * @param viewType 该adapter自己的type
     */
    @NonNull
    BaseViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType);

    /**
     * @param position 属于该adapter的position
     *                 如：{@link #getItemCount()}=1(每个bean只对应一条数据)，这个position一直是0（就是没用的意思）
     *                 如：{@link #getItemCount()}=xx(你的bean里面还有自己的list)，这个position就是相对的值
     */
    void bindViewHolder(@NonNull BaseViewHolder holder, int position);

    default int getSpanSize(int position) {
        return 1;
    }

    /**
     * @param position 相对的position
     * @return 不能超出范围, 超出就会被当成其他adapter的type(如果真的不够用可以自行下载修改min和max就行了)
     */
    @IntRange(from = BaseContainerAdapter.TYPE_MIN, to = BaseContainerAdapter.TYPE_MAX)
    default int getItemViewType(int position) {
        return 0;
    }

    /**
     * 由container调用
     *
     * @throws ClassCastException 请检查你list里的bean对象和adapter的bean是否一致
     */
    void setCurrentBean(@NonNull BEAN bean);

    /**
     * @return 当前的bean，每次想用的时候get就对了
     * 在回调里请用这个{@link OnItemClickListener#getCurrentBean}
     * 用的的地方：{@link #getItemCount}{@link #createViewHolder}{@link #bindViewHolder}{@link #getSpanSize}{@link #getItemViewType}...
     */
    @NonNull
    BEAN getCurrentBean();

    /**
     * 由container调用
     */
    void setCurrentPositionInfo(@NonNull ItemAdapterPositionInfo info);

    /**
     * 当前position额外附加的数据，方便adapter使用
     * 子类能使用的地方{@link #bindViewHolder}{@link #getItemViewType}{@link #getSpanSize}
     * 或者手动调用{@link BaseContainerAdapter#getItemAdapterPositionInfo}也行
     * <p>
     * 使用场景：有header展示线条，没header去掉线条；第一条展示红色，最后一条展示黑色
     */
    @NonNull
    ItemAdapterPositionInfo getCurrentPositionInfo();
}
