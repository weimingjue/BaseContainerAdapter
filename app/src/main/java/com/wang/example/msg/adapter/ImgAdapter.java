package com.wang.example.msg.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.container.adapter.OneContainerItemAdapter;
import com.wang.container.holder.BaseViewHolder;
import com.wang.container.interfaces.OnItemClickListener;
import com.wang.example.msg.bean.ImgBean;
import com.wang.example.utils.ToastUtils;

public class ImgAdapter extends OneContainerItemAdapter<ViewDataBinding, ImgBean> {

    public ImgAdapter() {
        setOnItemClickListener(new OnItemClickListener<ImgBean>() {
            @Override
            public void onItemClick(@NonNull View view, int position) {
                ToastUtils.toast("您点击了图片，绝对位置：" + getViewHolder(view).getCommonPosition());
            }
        });
    }

    @Override
    protected BaseViewHolder<ViewDataBinding> onCreateChildViewHolder(ViewGroup parent) {
        AppCompatImageView iv = new AppCompatImageView(parent.getContext());
        iv.setLayoutParams(new RecyclerView.LayoutParams(300, 300));
        iv.setPadding(0, 80, 0, 80);
        return new BaseViewHolder<>(iv);
    }

    @Override
    protected void onBindChildViewHolder(@NonNull BaseViewHolder<ViewDataBinding> holder, ImgBean bean) {
        ImageView iv = (ImageView) holder.itemView;
        iv.setImageResource(getCurrentBean().imgInfo.imgRes);
    }
}
