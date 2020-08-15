package com.wang.example.msg.bean;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.wang.container.adapter.IContainerItemAdapter;
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

    @NonNull
    @Override
    public Class<? extends IContainerItemAdapter> getBindAdapterClass() {
        return TextAdapter.class;
    }
}
