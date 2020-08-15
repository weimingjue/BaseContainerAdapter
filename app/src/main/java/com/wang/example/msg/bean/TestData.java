package com.wang.example.msg.bean;

import com.alibaba.fastjson.JSON;
import com.wang.example.R;

import java.util.ArrayList;
import java.util.List;

public class TestData {

    /**
     * 生成测试数据
     */
    public static List<BaseMsgBean> createMsgList() {
        ArrayList<BaseMsgBean> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            String json;
            switch (i % 5) {
                case 0:
                    json = createText(i);
                    break;
                case 1:
                    json = createImg(i);
                    break;
                case 2:
                    json = createOrder(i, 1);
                    break;
                case 3:
                    json = createOrder(i, 2);
                    break;
                default:
                    json = createOrder(i, 3);
                    break;
            }
            list.add(JSON.parseObject(json, BaseMsgBean.class));
        }
        return list;
    }

    private static String createText(int position) {
        return "{\"msgType\":1,\"data\":\"{\\\"text\\\":\\\"" + position + "\\\"}\"}";
    }

    private static String createImg(int position) {
        return "{\"msgType\":2,\"data\":\"{\\\"imgRes\\\":" + R.mipmap.ic_launcher + "}\"}";
    }

    private static String createOrder(int position, int orderType) {
        return "{\"msgType\":3,\"data\":\"{\\\"orderType\\\":" + orderType + ",\\\"orderNo\\\":\\\"0000" + position + "\\\",\\\"orderName\\\":\\\"哈哈\\\",\\\"otherOrderData\\\":{\\\"emsNo\\\":\\\"11\\\"}}\"}";
    }
}
