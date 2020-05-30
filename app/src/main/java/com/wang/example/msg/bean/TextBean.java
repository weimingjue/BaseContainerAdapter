package com.wang.example.msg.bean;

import com.alibaba.fastjson.JSON;
import com.wang.adapters.adapter.IContainerItemAdapter;
import com.wang.example.msg.adapter.TextAdapter;

public class TextBean extends BaseMsgBean {
    public TextDataEntity textInfo;

    protected TextBean(BaseMsgBean oldBean) {
        super(oldBean);
        textInfo = JSON.parseObject(oldBean.data, TextDataEntity.class);
    }

    public static class TextDataEntity {
        public String text;
    }

    @Override
    public Class<? extends IContainerItemAdapter> getItemAdapterClass() {
        return TextAdapter.class;
    }
}
