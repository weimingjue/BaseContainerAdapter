package com.wang.container.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;

import com.wang.container.BaseContainerAdapter;
import com.wang.container.R;
import com.wang.container.bean.IContainerBean;
import com.wang.container.bean.ItemAdapterPositionInfo;
import com.wang.container.holder.BaseViewHolder;
import com.wang.container.interfaces.OnItemClickListener;
import com.wang.container.observer.IContainerObserver;

/**
 * {@link IContainerItemAdapter}的抽象类
 */
public abstract class BaseContainerItemAdapter<BEAN extends IContainerBean> implements IContainerItemAdapter<BEAN> {

    protected ArraySet<IContainerObserver> mObservers = new ArraySet<>();

    protected MyItemClickListenerWrap mListener = new MyItemClickListenerWrap();

    private BEAN mCurrentBean;
    private ItemAdapterPositionInfo mCurrentPositionInfo;
    private BaseContainerAdapter mContainerAdapter;

    private BaseViewHolder mBindTempHolder;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 继承下来的基本实现,正常情况不需要再重写
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void registerDataSetObserver(@NonNull IContainerObserver observer) {
        mObservers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(@NonNull IContainerObserver observer) {
        mObservers.remove(observer);
    }

    @NonNull
    @Override
    public final BaseViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder holder = onCreateViewHolder(parent, viewType);
        holder.itemView.setTag(R.id.tag_view_holder, holder);
        holder.itemView.setTag(R.id.tag_view_adapter, this);
        holder.itemView.setTag(R.id.tag_view_container, mContainerAdapter);
        return holder;
    }

    @Override
    public final void bindViewHolder(@NonNull BaseViewHolder holder, int position) {
        mBindTempHolder = holder;
        holder.itemView.setTag(R.id.tag_view_bean, getCurrentBean());
        holder.itemView.setOnClickListener(mListener);
        holder.itemView.setOnLongClickListener(mListener);
        onBindViewHolder(holder, position);
    }

    @Override
    public BaseViewHolder getBindTempViewHolder() {
        return mBindTempHolder;
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener<BEAN> listener) {
        mListener.setListener(listener);
        notifyDataSetChanged();
    }

    /**
     * @return 返回的是wrap，如果想得到自己的listener，再get一下{@link MyItemClickListenerWrap#getListener}
     */
    @Nullable
    @Override
    public MyItemClickListenerWrap getOnItemClickListener() {
        return mListener;
    }

    @Override
    public void notifyDataSetChanged() {
        for (IContainerObserver observer : mObservers) {
            observer.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyItemChanged(int positionStart, int itemCount, @NonNull BEAN bean) {
        for (IContainerObserver observer : mObservers) {
            observer.notifyItemChanged(positionStart, itemCount, bean);
        }
    }

    @Override
    public void notifyItemInserted(int positionStart, int itemCount, @NonNull BEAN bean) {
        for (IContainerObserver observer : mObservers) {
            observer.notifyItemInserted(positionStart, itemCount, bean);
        }
    }

    @Override
    public void notifyItemMoved(int fromPosition, int toPosition, @NonNull BEAN bean) {
        for (IContainerObserver observer : mObservers) {
            observer.notifyItemMoved(fromPosition, toPosition, bean);
        }
    }

    @Override
    public void notifyItemRemoved(int positionStart, int itemCount, @NonNull BEAN bean) {
        for (IContainerObserver observer : mObservers) {
            observer.notifyItemRemoved(positionStart, itemCount, bean);
        }
    }

    /**
     * 由container调用
     *
     * @throws ClassCastException 请检查你list里的bean对象和adapter的bean是否一致
     */
    @Override
    public void setCurrentBean(@NonNull BEAN bean) {
        mCurrentBean = bean;
    }

    /**
     * 由container调用
     */
    public void setCurrentPositionInfo(@NonNull ItemAdapterPositionInfo info) {
        mCurrentPositionInfo = info;
    }

    @Override
    public void attachContainer(@NonNull BaseContainerAdapter containerAdapter) {
        mContainerAdapter = containerAdapter;
    }

    /**
     * {@link #setOnItemClickListener}{@link BaseContainerAdapter#setOnItemClickListener}这两个listener的事件分发
     */
    protected class MyItemClickListenerWrap implements OnItemClickListener<BEAN> {

        /**
         * {@link #setOnItemClickListener}
         */
        @Nullable
        private OnItemClickListener<BEAN> mListener;

        @Override
        public void onClick(@NonNull View view) {
            for (IContainerObserver observer : mObservers) {
                observer.dispatchItemClicked(view);
            }

            OnItemClickListener.super.onClick(view);
        }

        @Override
        public boolean onLongClick(@NonNull View view) {
            boolean state = false;
            for (IContainerObserver observer : mObservers) {
                state = state | observer.dispatchItemLongClicked(view);
            }

            state = state | OnItemClickListener.super.onLongClick(view);
            return state;
        }

        @Override
        public void onItemClick(@NonNull View view, int position) {
            if (mListener != null) {
                mListener.onItemClick(view, position);
            }
        }

        @Override
        public boolean onItemLongClick(@NonNull View view, int position) {
            if (mListener != null) {
                return mListener.onItemLongClick(view, position);
            }
            return false;
        }

        public void setListener(@Nullable OnItemClickListener<BEAN> listener) {
            mListener = listener;
        }

        @Nullable
        public OnItemClickListener<BEAN> getListener() {
            return mListener;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 以下是经常用到或重写的方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @return 当前的bean，每次想用的时候get就对了
     * 在回调里请用这个{@link OnItemClickListener#getCurrentBean}
     * 用的的地方：{@link #getItemCount}{@link #onCreateViewHolder}{@link #onBindViewHolder}{@link #getSpanSize}{@link #getItemViewType}...
     */
    @NonNull
    @Override
    public final BEAN getCurrentBean() {
        return mCurrentBean;
    }

    /**
     * 当前position额外附加的数据，方便adapter使用
     * 子类能使用的地方{@link #bindViewHolder}{@link #getItemViewType}{@link #getSpanSize}
     * 或者手动调用{@link BaseContainerAdapter#getItemAdapterPositionInfo}也行
     * <p>
     * 使用场景：有header展示线条，没header去掉线条；第一条展示红色，最后一条展示黑色
     */
    @NonNull
    @Override
    public ItemAdapterPositionInfo getCurrentPositionInfo() {
        return mCurrentPositionInfo;
    }

    /**
     * 返回容器（会在{@link BaseContainerAdapter#addAdapter}立即调用,正常使用不会为null）
     */
    @NonNull
    public BaseContainerAdapter getContainerAdapter() {
        return mContainerAdapter;
    }

//    public int getItemCount() {}

//    public int getSpanSize(int position) {}

//    public int getItemViewType(int position) {}

    /**
     * @param viewType 该adapter自己的type
     */
    @NonNull
    protected abstract BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    /**
     * 想取绝对position见{@link BaseContainerAdapter#getAbsPosition}
     *
     * @param position 属于该adapter的position
     *                 如：{@link #getItemCount()}=1(每个bean只对应一条数据)，这个position一直是0（就是没用的意思）
     *                 如：{@link #getItemCount()}=xx(你的bean里面还有自己的list)，这个position就是相对的值
     */
    protected abstract void onBindViewHolder(@NonNull BaseViewHolder holder, int position);
}
