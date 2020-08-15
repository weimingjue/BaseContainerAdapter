package com.wang.container.interfaces;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wang.container.holder.BaseViewHolder;

/**
 * 所有adapter的接口
 */
public interface IAdapter<LISTENER extends IItemClick> {

    int getItemCount();

    default int getItemViewType(int position) {
        return 0;
    }

    void setOnItemClickListener(@Nullable LISTENER listener);

    void notifyDataSetChanged();

    @NonNull
    BaseViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType);

    void bindViewHolder(BaseViewHolder holder, int position);
}
