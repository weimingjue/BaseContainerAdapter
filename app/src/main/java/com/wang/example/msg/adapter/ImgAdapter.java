package com.wang.example.msg.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.adapters.adapter.BaseContainerItemAdapter;
import com.wang.adapters.listener.OnItemClickListener;
import com.wang.example.MyApplication;
import com.wang.example.msg.bean.ImgBean;

public class ImgAdapter extends BaseContainerItemAdapter<RecyclerView.ViewHolder, ImgBean> {

    public ImgAdapter() {
        setOnItemClickListener(new OnItemClickListener<ImgBean>() {
            @Override
            protected void onItemClick(View view, int position) {
                Toast.makeText(MyApplication.getContext(), "您点击了图片", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ImageView iv = (ImageView) holder.itemView;
        iv.setImageResource(getCurrentBean().imgInfo.imgRes);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType, LayoutInflater inflater) {
        AppCompatImageView iv = new AppCompatImageView(parent.getContext());
        iv.setLayoutParams(new RecyclerView.LayoutParams(300, 300));
        iv.setPadding(0, 80, 0, 80);
        return new RecyclerView.ViewHolder(iv) {
        };
    }
}
