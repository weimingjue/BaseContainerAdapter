package com.wang.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.adapters.BaseContainerAdapter;
import com.wang.example.msg.adapter.ImgAdapter;
import com.wang.example.msg.adapter.NoSupportAdapter;
import com.wang.example.msg.adapter.PaySuccessOrderAdapter;
import com.wang.example.msg.adapter.TextAdapter;
import com.wang.example.msg.adapter.WaitPayOrderAdapter;
import com.wang.example.msg.bean.BaseMsgBean;
import com.wang.example.msg.bean.TestData;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRv;
    private BaseContainerAdapter<BaseMsgBean> mBaseAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMsg();
            }
        });
        findViewById(R.id.tv_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        findViewById(R.id.tv_reverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.reverse(mBaseAdapter.getList());
                mBaseAdapter.notifyDataSetChanged();
            }
        });
        mBaseAdapter = new BaseContainerAdapter<BaseMsgBean>();
        mRv = findViewById(R.id.rv_main);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(mBaseAdapter);
    }

    private void createMsg() {
        mBaseAdapter.addAdapter(new TextAdapter(), new ImgAdapter(), new WaitPayOrderAdapter(),
                new PaySuccessOrderAdapter(), new PaySuccessOrderAdapter(), new NoSupportAdapter());

        final List<BaseMsgBean> list = TestData.createMsgList();//模拟请求数据
        List<BaseMsgBean> newList = BaseMsgBean.formatListData(list);//格式化数据
        mBaseAdapter.setListAndNotifyDataSetChanged(newList);
    }


    public void startActivity(Class c) {
        startActivity(new Intent(this, c));
    }
}
