package com.wang.adapters.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.adapters.BaseContainerAdapter;
import com.wang.adapters.bean.IContainerBean;
import com.wang.adapters.observer.IContainerObserver;
import com.wang.adapters.listener.IItemClick;
import com.wang.adapters.listener.OnItemClickListener;

/**
 * adapter接口,见实现类{@link BaseContainerItemAdapter}
 * 和普通adapter操作一样，加了个{@link #getCurrentBean}来确定当前adapter的数据
 */
public interface IContainerItemAdapter<VH extends RecyclerView.ViewHolder, BEAN extends IContainerBean> extends IContainerObserver {
    /**
     * observe主要用于notify
     */
    void registerDataSetObserver(@NonNull IContainerObserver observer);

    void unregisterDataSetObserver(@NonNull IContainerObserver observer);

    int getItemCount();

    /**
     * @param viewType 该adapter自己的type
     */
    @NonNull
    VH createViewHolder(@NonNull ViewGroup parent, int viewType, LayoutInflater inflater);

    /**
     * absPosition用不到，就不给了
     *
     * @param position 属于该adapter的position
     */
    void bindViewHolder(@NonNull VH holder, int position);

    int getSpanSize(int position);

    /**
     * @return 不能超出范围, 超出就会被当成其他adapter的type(如果仍不够使用可以自行修改{@link BaseContainerAdapter#TYPE_MAX},{@link BaseContainerAdapter#TYPE_MIN}的值)
     */
    @IntRange(from = BaseContainerAdapter.TYPE_MIN, to = BaseContainerAdapter.TYPE_MAX)
    int getItemViewType(int position);

    /**
     * @param listener {@link OnItemClickListener}
     */
    void setOnItemClickListener(IItemClick<BEAN> listener);

    void setCurrentBean(@NonNull BEAN bean);

    /**
     * @return 当前的bean，每次想用的时候get就对了
     * 在回调里请用这个{@link OnItemClickListener#getCurrentBean}
     * 用的的地方：{@link #getItemCount}{@link #createViewHolder}{@link #bindViewHolder}{@link #getSpanSize}{@link #getItemViewType}...
     */
    @NonNull
    BEAN getCurrentBean();
}
