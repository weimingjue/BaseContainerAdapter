package com.wang.container.interfaces;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.wang.container.holder.BaseViewHolder;

import java.util.Collection;
import java.util.List;

/**
 * 所有list的adapter的接口
 */
public interface IListAdapter<BEAN, DB extends ViewDataBinding, LISTENER extends IItemClick> extends IAdapter<LISTENER> {

    /**
     * @param view null表示删除
     */
    void setHeaderView(@Nullable View view);

    @Nullable
    View getHeaderView();

    /**
     * @param view null表示删除
     */
    void setFooterView(@Nullable View view);

    @Nullable
    View getFooterView();

    /**
     * 最终你的list的create
     * <p>
     * 默认用DataBinding create
     * 完全不需要的话覆盖整个方法就行了，不会出问题
     * 你也可以重写来添加自己的默认逻辑，如：全局隐藏显示、嵌套rv的默认属性设置等
     */
    @NonNull
    BaseViewHolder<DB> onCreateListViewHolder(@NonNull ViewGroup parent);


    /**
     * 最终你的list的bind
     * <p>
     * 你只需要处理其他数据、其他点击事件等，基本的都加上了：
     * <p>
     * 不需要绑定bean数据，已经默认绑定
     * 不需要调用{@link ViewDataBinding#executePendingBindings()}，已经默认加上
     *
     * @param listPosition 已经做过处理,就是list的position
     */
    void onBindListViewHolder(@NonNull BaseViewHolder<DB> holder, int listPosition, BEAN bean);


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // list相关的方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @NonNull
    List<BEAN> getList();

    default void setListAndNotifyDataSetChanged(@Nullable List<BEAN> list) {
        getList().clear();
        if (list != null) {
            getList().addAll(list);
        }
        notifyDataSetChanged();
    }

    /**
     * 获取指定bean
     *
     * @throws IndexOutOfBoundsException 不用多说吧
     */
    @NonNull
    default BEAN get(int listPosition) {
        return getList().get(listPosition);
    }

    /**
     * 清空list,不刷新adapter
     */
    default void clear() {
        getList().clear();
    }

    /**
     * 添加全部条目,不刷新adapter
     */
    default void addAll(@Nullable Collection<? extends BEAN> addList) {
        if (addList != null) {
            getList().addAll(addList);
        }
    }

    default int size() {
        return getList().size();
    }

    /**
     * list是否为空
     */
    default boolean isEmptyList() {
        return getList().isEmpty();
    }
}
