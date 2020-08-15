package com.wang.example.msg.bean;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.wang.container.adapter.IContainerItemAdapter;
import com.wang.example.msg.adapter.ImgAdapter;

public class ImgBean extends BaseMsgBean {
    public ImgDataEntity imgInfo;

    protected ImgBean(BaseMsgBean oldBean) {
        super(oldBean);
        imgInfo = JSON.parseObject(oldBean.data, ImgDataEntity.class);
    }

    public static class ImgDataEntity {
        public int imgRes;
    }

    @NonNull
    @Override
    public Class<? extends IContainerItemAdapter> getBindAdapterClass() {
        return ImgAdapter.class;
    }
}
