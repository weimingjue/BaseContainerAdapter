package com.wang.adapters.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.adapters.BaseContainerAdapter;
import com.wang.adapters.R;
import com.wang.adapters.listener.IItemClick;
import com.wang.adapters.bean.IContainerBean;
import com.wang.adapters.listener.OnItemClickListener;
import com.wang.adapters.observer.IContainerObserver;

/**
 * {@link IContainerItemAdapter}的抽象类
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseContainerItemAdapter<VH extends RecyclerView.ViewHolder, BEAN extends IContainerBean> implements IContainerItemAdapter<VH, BEAN> {

    protected ArraySet<IContainerObserver> mObservers = new ArraySet<>();

    protected IItemClick<BEAN> mListener;
    private BEAN mCurrentBean;

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

    @Override
    public final void bindViewHolder(@NonNull VH holder, int position) {
        holder.itemView.setTag(R.id.tag_view_click, position);
        holder.itemView.setTag(R.id.tag_view_bean, getCurrentBean());
        holder.itemView.setOnClickListener(mListener);
        holder.itemView.setOnLongClickListener(mListener);
        onBindViewHolder(holder, position);
    }

    @NonNull
    @Override
    public final VH createViewHolder(@NonNull ViewGroup parent, int viewType, LayoutInflater inflater) {
        VH holder = onCreateViewHolder(parent, viewType, inflater);
        //保存holder，如果position无法解决问题，可以使用这个
        holder.itemView.setTag(R.id.tag_view_holder, holder);
        return holder;
    }

    @Override
    public void setOnItemClickListener(IItemClick<BEAN> listener) {
        mListener = listener;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        for (IContainerObserver observer : mObservers) {
            observer.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyItemChanged(int position, IContainerBean bean) {
        notifyItemChanged(position, 1, bean);
    }

    @Override
    public void notifyItemChanged(int positionStart, int itemCount, IContainerBean bean) {
        for (IContainerObserver observer : mObservers) {
            observer.notifyItemChanged(positionStart, itemCount, bean);
        }
    }

    @Override
    public void notifyItemInserted(int position, IContainerBean bean) {
        notifyItemInserted(position, 1, bean);
    }

    @Override
    public void notifyItemInserted(int positionStart, int itemCount, IContainerBean bean) {
        for (IContainerObserver observer : mObservers) {
            observer.notifyItemInserted(positionStart, itemCount, bean);
        }
    }

    @Override
    public void notifyItemMoved(int fromPosition, int toPosition, IContainerBean bean) {
        for (IContainerObserver observer : mObservers) {
            observer.notifyItemMoved(fromPosition, toPosition, bean);
        }
    }

    @Override
    public void notifyItemRemoved(int position, IContainerBean bean) {
        notifyItemRemoved(position, 1, bean);
    }

    @Override
    public void notifyItemRemoved(int positionStart, int itemCount, IContainerBean bean) {
        for (IContainerObserver observer : mObservers) {
            observer.notifyItemRemoved(positionStart, itemCount, bean);
        }
    }

    @Override
    public void setCurrentBean(@NonNull BEAN bean) {
        mCurrentBean = bean;
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

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getSpanSize(int position) {
        return 1;
    }

    /**
     * @return 不能超出范围, 超出就会被当成其他adapter的type(如果仍不够使用可以自行修改{@link BaseContainerAdapter#TYPE_MAX},{@link BaseContainerAdapter#TYPE_MIN}的值)
     */
    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    /**
     * absPosition用不到，就不给了
     *
     * @param position 属于该adapter的position
     */
    protected abstract void onBindViewHolder(@NonNull VH holder, int position);

    /**
     * @param viewType 该adapter自己的type
     */
    protected abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType, LayoutInflater inflater);
}
