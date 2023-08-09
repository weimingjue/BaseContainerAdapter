package com.wang.example.utils

import android.widget.Toast
import com.wang.example.MyApplication

fun String.toast() {
    Toast.makeText(MyApplication.context, this, Toast.LENGTH_SHORT).show()
}