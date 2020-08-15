package com.wang.example.msg.bean;

import androidx.annotation.NonNull;

import com.wang.container.adapter.IContainerItemAdapter;
import com.wang.container.bean.IContainerBean;
import com.wang.example.msg.adapter.UnsupportedAdapter;

import java.util.ArrayList;
import java.util.List;

public class BaseMsgBean implements IContainerBean {
    public int msgType;//1文字，2图片，3订单（data内有orderType：1未支付，2已支付，3新状态）
    public String data;//type对应的数据

    public BaseMsgBean() {
    }

    protected BaseMsgBean(BaseMsgBean oldBean) {
        msgType = oldBean.msgType;
        data = oldBean.data;
    }

    @NonNull
    @Override
    public Class<? extends IContainerItemAdapter> getBindAdapterClass() {
        return UnsupportedAdapter.class;
    }

    /**
     * 将bean改成对应的实现类
     */
    public static List<BaseMsgBean> formatListData(List<BaseMsgBean> oldList) {
        ArrayList<BaseMsgBean> list = new ArrayList<>();
        for (BaseMsgBean bean : oldList) {
            BaseMsgBean newBean;
            switch (bean.msgType) {
                case 1:
                    newBean = new TextBean(bean);
                    break;
                case 2:
                    newBean = new ImgBean(bean);
                    break;
                case 3:
                    newBean = new OrderBean(bean);
                    break;
                default:
                    newBean = new BaseMsgBean(bean);
                    break;
            }
            list.add(newBean);
        }
        return list;
    }
}
