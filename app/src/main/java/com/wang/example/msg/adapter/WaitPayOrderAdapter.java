package com.wang.example.msg.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.wang.container.adapter.OneContainerItemAdapter;
import com.wang.container.bean.ItemAdapterPositionInfo;
import com.wang.container.holder.BaseViewHolder;
import com.wang.container.interfaces.OnItemClickListener;
import com.wang.example.R;
import com.wang.example.databinding.AdapterMsgWaitPayOrderBinding;
import com.wang.example.msg.bean.OrderBean;
import com.wang.example.utils.ToastUtils;

public class WaitPayOrderAdapter extends OneContainerItemAdapter<AdapterMsgWaitPayOrderBinding, OrderBean> {

    public WaitPayOrderAdapter() {
        setOnItemClickListener(new OnItemClickListener<OrderBean>() {
            @Override
            public void onItemClick(@NonNull View view, int position) {
                switch (view.getId()) {
                    case R.id.bt_state:
                        ToastUtils.toast("您点击了列表状态，绝对位置：" + getViewHolder(view).getCommonPosition());
                        break;
                    default:
                        ToastUtils.toast("您点击了待支付条目，绝对位置：" + getViewHolder(view).getCommonPosition());
                        break;
                }
            }
        });
    }

    @Override
    protected void onBindChildViewHolder(@NonNull BaseViewHolder<AdapterMsgWaitPayOrderBinding> holder, OrderBean bean) {
        String text = "列表状态：";
        int absState = getCurrentPositionInfo().mAbsState;
        if ((absState & ItemAdapterPositionInfo.ABS_STATE_FIRST_LIST_POSITION) != 0) {
            text += "整个列表第一个";
        }
        if ((absState & ItemAdapterPositionInfo.ABS_STATE_LAST_LIST_POSITION) != 0) {
            text += "整个列表最后一个";
        }
        if ((absState & ItemAdapterPositionInfo.ABS_STATE_CENTER_POSITION) != 0) {
            text += "列表中间";
        }
        holder.getBinding().btState.setText(text);
    }
}
