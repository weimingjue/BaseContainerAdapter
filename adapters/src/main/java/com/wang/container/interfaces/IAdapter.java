package com.wang.container.interfaces;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.container.R;
import com.wang.container.holder.BaseViewHolder;

import java.util.List;

/**
 * 所有adapter的接口
 */
public interface IAdapter<LISTENER extends IItemClick> {

    int getItemCount();

    default int getItemViewType(int position) {
        return 0;
    }

    void setOnItemClickListener(@Nullable LISTENER listener);

    @Nullable
    LISTENER getOnItemClickListener();

    void notifyDataSetChanged();

    @NonNull
    BaseViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType);

    void bindViewHolder(@NonNull BaseViewHolder holder, int position);

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // xml的拓展功能，如不明白可直接忽略
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 正在bind时的ViewHolder，方便xml中使用dataBinding设置点击事件
     * 只能在bind时使用，否则肯定是错的
     */
    BaseViewHolder getBindTempViewHolder();

    /**
     * 给view设置点击事件到{@link #getOnItemClickListener()}中
     * <p>
     * 点击回调见{@link #setOnItemClickListener}{@link OnItemClickListener}
     */
    @CallSuper
    default void setItemViewClick(@NonNull View view, @NonNull BaseViewHolder holder) {
        view.setTag(R.id.tag_view_holder, holder);//view.setTag(R.id.tag_view_holder, holder.itemView.getTag(R.id.tag_view_holder));
        view.setTag(R.id.tag_view_bean, holder.itemView.getTag(R.id.tag_view_bean));
        view.setTag(R.id.tag_view_adapter, this);//view.setTag(R.id.tag_view_adapter, holder.itemView.getTag(R.id.tag_view_adapter));
        view.setTag(R.id.tag_view_container, holder.itemView.getTag(R.id.tag_view_container));
        if (!(view instanceof RecyclerView)) {
            view.setOnClickListener(getOnItemClickListener());
            view.setOnLongClickListener(getOnItemClickListener());
        }
    }

    /**
     * Android Studio在jar里的BindingAdapter居然不会自动提示，目前只能到自己项目里再写一遍BindingAdapter了
     */
    @BindingAdapter({"setItemViewClick"})
    static void setItemViewClickBinding(@NonNull View view, @NonNull IAdapter adapter) {
        adapter.setItemViewClick(view, adapter.getBindTempViewHolder());
    }

    /**
     * （container暂时不支持）
     * 给rv设置点击事件和数据
     * 点击回调必须使用{@link com.wang.adapters.interfaces.OnItemItemClickListener}，否则回调将会错乱
     * 见：https://github.com/weimingjue/BaseAdapter
     */
    @CallSuper
    default void setItemRvData(@NonNull RecyclerView rv, @NonNull BaseViewHolder holder, @Nullable List<?> adapterList) {
        rv.setTag(R.id.tag_view_holder, holder);//view.setTag(R.id.tag_view_holder, holder.itemView.getTag(R.id.tag_view_holder));
        rv.setTag(R.id.tag_view_bean, holder.itemView.getTag(R.id.tag_view_bean));
        rv.setTag(R.id.tag_view_adapter, this);//view.setTag(R.id.tag_view_adapter, holder.itemView.getTag(R.id.tag_view_adapter));
        rv.setTag(R.id.tag_view_container, holder.itemView.getTag(R.id.tag_view_container));
        IListAdapter adapter = (IListAdapter) rv.getAdapter();
        //noinspection ConstantConditions,unchecked
        adapter.setOnItemClickListener(getOnItemClickListener());
        //noinspection unchecked 忽略未检查错误,如果出异常说明你传的list和你的adapter对不上
        adapter.setListAndNotifyDataSetChanged(adapterList);
    }

    @BindingAdapter({"setItemRvDataAdapter", "setItemRvDataList"})
    static void setItemRvDataBinding(@NonNull RecyclerView rv, @NonNull IAdapter<?> adapter, @Nullable List<?> adapterList) {
        adapter.setItemRvData(rv, adapter.getBindTempViewHolder(), adapterList);
    }
}
