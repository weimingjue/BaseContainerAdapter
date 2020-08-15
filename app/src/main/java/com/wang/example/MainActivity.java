package com.wang.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.container.BaseContainerAdapter;
import com.wang.container.interfaces.OnItemClickListener;
import com.wang.example.msg.adapter.ImgAdapter;
import com.wang.example.msg.adapter.PaySuccessOrderAdapter;
import com.wang.example.msg.adapter.TextAdapter;
import com.wang.example.msg.adapter.UnsupportedAdapter;
import com.wang.example.msg.adapter.WaitPayOrderAdapter;
import com.wang.example.msg.bean.BaseMsgBean;
import com.wang.example.msg.bean.TestData;
import com.wang.example.utils.ToastUtils;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRv;
    @Nullable
    private BaseContainerAdapter mBaseAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_msg).setOnClickListener(v -> createMsg());
        findViewById(R.id.tv_other).setOnClickListener(v -> {
            ToastUtils.toast("暂时没精力写");
        });
        findViewById(R.id.tv_reverse).setOnClickListener(v -> {
            if (mBaseAdapter != null) {
                Collections.reverse(mBaseAdapter.getList());
                mBaseAdapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.tv_delete).setOnClickListener(v -> {
            if (mBaseAdapter != null) {
                int random = (int) (Math.random() * (Math.min(mBaseAdapter.size(), 5)));
                if (random < mBaseAdapter.size()) {
                    mBaseAdapter.getList().remove(random);
                    //这里需要注意，header的存在
                    mBaseAdapter.notifyItemRemoved(mBaseAdapter.getHeaderView() == null ? random : random + 1);
                    ToastUtils.toast("你删除了list position：" + random);

                    //remove动画不会改变第一条和最后一条的状态，所以带header、footer提示的话也要同步刷新
                    mBaseAdapter.notifyItemChanged(mBaseAdapter.getHeaderView() == null ? 0 : 1);
                    mBaseAdapter.notifyItemChanged(mBaseAdapter.getHeaderView() == null ? mBaseAdapter.size() - 1 : mBaseAdapter.size());
                }
            }
        });

        mRv = findViewById(R.id.rv_main);
        mRv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void createMsg() {
        final BaseContainerAdapter<BaseMsgBean> baseAdapter = new BaseContainerAdapter<>();
        baseAdapter.addAdapter(new TextAdapter(), new ImgAdapter(), new WaitPayOrderAdapter(),
                new PaySuccessOrderAdapter(), new UnsupportedAdapter());

        //header、footer
        TextView headerView = new TextView(this);
        headerView.setText("这是header");
        headerView.setGravity(Gravity.CENTER);
        headerView.setPadding(50, 50, 50, 50);
        TextView footerView = new TextView(this);
        footerView.setText("这是footer");
        footerView.setGravity(Gravity.CENTER);
        footerView.setPadding(50, 50, 50, 50);
        footerView.setOnClickListener(v -> ToastUtils.toast("你点击了footer"));
        baseAdapter.setHeaderView(headerView);
        baseAdapter.setFooterView(footerView);

        final List<BaseMsgBean> list = TestData.createMsgList();//模拟请求数据
        List<BaseMsgBean> newList = BaseMsgBean.formatListData(list);//格式化数据
        baseAdapter.setListAndNotifyDataSetChanged(newList);

        //和子adapter的事件都调用会触发，注意自己的逻辑别和子adapter重复
        baseAdapter.setOnItemClickListener(new OnItemClickListener<BaseMsgBean>() {
            @Override
            public void onItemClick(@NonNull View view, int position) {
                int absPosition = baseAdapter.getAbsPosition(getCurrentBean(), position);
                ToastUtils.toast("Base的点击事件，绝对位置：" + absPosition);
            }
        });
        mRv.setAdapter(baseAdapter);
        mBaseAdapter = baseAdapter;
    }


    public void startActivity(Class c) {
        startActivity(new Intent(this, c));
    }
}
