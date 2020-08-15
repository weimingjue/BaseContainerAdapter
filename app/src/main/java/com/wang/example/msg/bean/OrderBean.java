package com.wang.example.msg.bean;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.wang.container.adapter.IContainerItemAdapter;
import com.wang.example.msg.adapter.PaySuccessOrderAdapter;
import com.wang.example.msg.adapter.WaitPayOrderAdapter;

public class OrderBean extends BaseMsgBean {
    public OrderDataEntity orderInfo;

    protected OrderBean(BaseMsgBean oldBean) {
        super(oldBean);
        orderInfo = JSON.parseObject(oldBean.data, OrderDataEntity.class);
    }

    public static class OrderDataEntity {
        public int orderType;//1未支付，2已支付
        public String orderNo, orderName;
        public OtherOrderData otherOrderData;

        public static class OtherOrderData {
            public String emsNo;
        }
    }

    @NonNull
    @Override
    public Class<? extends IContainerItemAdapter> getBindAdapterClass() {
        switch (orderInfo.orderType) {
            case 1:
                return WaitPayOrderAdapter.class;
            case 2:
                return PaySuccessOrderAdapter.class;
        }
        return super.getBindAdapterClass();
    }
}
