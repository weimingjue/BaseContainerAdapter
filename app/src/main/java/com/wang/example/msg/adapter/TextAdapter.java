package com.wang.example.msg.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.ViewDataBinding;

import com.wang.container.adapter.OneContainerItemAdapter;
import com.wang.container.bean.ItemAdapterPositionInfo;
import com.wang.container.holder.BaseViewHolder;
import com.wang.container.interfaces.OnItemClickListener;
import com.wang.example.msg.bean.TextBean;
import com.wang.example.utils.ToastUtils;

public class TextAdapter extends OneContainerItemAdapter<ViewDataBinding, TextBean> {
    public TextAdapter() {
        setOnItemClickListener(new OnItemClickListener<TextBean>() {
            @Override
            public void onItemClick(@NonNull View view, int position) {
                TextBean bean = getCurrentBean(view);
                ToastUtils.toast("您点击了文字：" + bean.textInfo.text + "，绝对位置：" + getViewHolder(view).getCommonPosition());
            }
        });
    }

    @Override
    protected BaseViewHolder<ViewDataBinding> onCreateChildViewHolder(ViewGroup parent) {
        AppCompatTextView tv = new AppCompatTextView(parent.getContext());
        tv.setTextSize(20);
        tv.setPadding(0, 80, 0, 80);
        return new BaseViewHolder<>(tv);
    }

    @Override
    protected void onBindChildViewHolder(@NonNull BaseViewHolder<ViewDataBinding> holder, TextBean bean) {
        TextView tv = (TextView) holder.itemView;
        String text = "这是文字：" + bean.textInfo.text;

        ItemAdapterPositionInfo info = getCurrentPositionInfo();
        if (info.isFirst()) {
            text += "，整个列表第一个";
        }
        if (info.isLast()) {
            text += "，整个列表最后一个";
        }
        tv.setText(text);
    }
}
