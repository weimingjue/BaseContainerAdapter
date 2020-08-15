package com.wang.example.utils;

import android.widget.Toast;

import com.wang.example.MyApplication;

public class ToastUtils {
    public static void toast(CharSequence cs) {
        Toast.makeText(MyApplication.getContext(), cs, Toast.LENGTH_SHORT).show();
    }
}
