package com.wang.example.msg.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.ViewDataBinding;

import com.wang.container.adapter.OneContainerItemAdapter;
import com.wang.container.bean.ItemAdapterPositionInfo;
import com.wang.container.holder.BaseViewHolder;
import com.wang.example.msg.bean.BaseMsgBean;
import com.wang.example.utils.ToastUtils;

public class UnsupportedAdapter extends OneContainerItemAdapter<ViewDataBinding, BaseMsgBean> {

    public UnsupportedAdapter() {
        setOnItemClickListener((view, position) -> ToastUtils.toast("您点击了新版本的新类型"));
    }

    @Override
    protected BaseViewHolder<ViewDataBinding> onCreateChildViewHolder(ViewGroup parent) {
        AppCompatTextView tv = new AppCompatTextView(parent.getContext());
        tv.setTextSize(15);
        tv.setPadding(0, 80, 0, 80);
        return new BaseViewHolder<>(tv);
    }

    @Override
    protected void onBindChildViewHolder(@NonNull BaseViewHolder<ViewDataBinding> holder, BaseMsgBean bean) {
        TextView tv = (TextView) holder.itemView;
        String text = "这是新版本的消息类型";

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
