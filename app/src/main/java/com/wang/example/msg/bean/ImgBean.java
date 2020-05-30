package com.wang.example.msg.bean;

import com.alibaba.fastjson.JSON;
import com.wang.adapters.adapter.IContainerItemAdapter;
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

    @Override
    public Class<? extends IContainerItemAdapter> getItemAdapterClass() {
        return ImgAdapter.class;
    }
}
