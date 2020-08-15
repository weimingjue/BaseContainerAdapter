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
import com.wang.example.msg.bean.OrderBean;
import com.wang.example.utils.ToastUtils;

public class PaySuccessOrderAdapter extends OneContainerItemAdapter<ViewDataBinding, OrderBean> {

    public PaySuccessOrderAdapter() {
        setOnItemClickListener(new OnItemClickListener<OrderBean>() {
            @Override
            public void onItemClick(@NonNull View view, int position) {
                OrderBean bean = getCurrentBean();
                ToastUtils.toast("您点击了支付成功订单，订单号：" + bean.orderInfo.orderNo);
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
    protected void onBindChildViewHolder(@NonNull BaseViewHolder<ViewDataBinding> holder, OrderBean bean) {
        TextView tv = (TextView) holder.itemView;
        String text = "支付成功，订单号：" + bean.orderInfo.orderNo + "，订单名称" + bean.orderInfo.orderName
                + "，物流：" + bean.orderInfo.otherOrderData.emsNo;

        int absState = getCurrentPositionInfo().mAbsState;
        if ((absState & ItemAdapterPositionInfo.ABS_STATE_FIRST_LIST_POSITION) != 0) {
            text += "，整个列表第一个";
        }
        if ((absState & ItemAdapterPositionInfo.ABS_STATE_LAST_LIST_POSITION) != 0) {
            text += "，整个列表最后一个";
        }
        tv.setText(text);
    }
}
