package com.wang.example.msg.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.adapters.adapter.BaseContainerItemAdapter;
import com.wang.adapters.listener.OnItemClickListener;
import com.wang.example.MyApplication;
import com.wang.example.msg.bean.BaseMsgBean;

public class NoSupportAdapter extends BaseContainerItemAdapter<RecyclerView.ViewHolder, BaseMsgBean> {

    public NoSupportAdapter() {
        setOnItemClickListener(new OnItemClickListener<BaseMsgBean>() {
            @Override
            protected void onItemClick(View view, int position) {
                Toast.makeText(MyApplication.getContext(), "您点击了新版本的新类型", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView tv = (TextView) holder.itemView;
        tv.setText("这是新版本的消息类型");
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType, LayoutInflater inflater) {
        AppCompatTextView tv = new AppCompatTextView(parent.getContext());
        tv.setTextSize(15);
        tv.setPadding(0, 80, 0, 80);
        return new RecyclerView.ViewHolder(tv) {
        };
    }
}
