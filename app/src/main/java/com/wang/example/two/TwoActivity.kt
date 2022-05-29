package com.wang.example.two

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.wang.container.BaseContainerAdapter
import com.wang.example.R
import com.wang.example.main.adapter.WaitPayOrderAdapter
import com.wang.example.main.bean.TestData
import com.wang.example.two.adapter.HomeBannerAdapter
import com.wang.example.two.adapter.HomeGoodsAdapter
import com.wang.example.two.adapter.HomeTextAdapter
import com.wang.example.two.adapter.HomeUnsupportedAdapter
import com.wang.example.two.bean.HomeBaseBean
import com.wang.example.utils.toast

class TwoActivity : AppCompatActivity() {

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two)
        val baseAdapter = BaseContainerAdapter<HomeBaseBean>()
        findViewById<View>(R.id.tv_reverse).setOnClickListener {
            baseAdapter.list.reverse()
            baseAdapter.notifyDataSetChanged()
        }
        val rv = findViewById<RecyclerView>(R.id.rv_two)
        baseAdapter.addAdapter(
            HomeBannerAdapter(), HomeTextAdapter(), HomeGoodsAdapter(), HomeUnsupportedAdapter()
        )

        //header、footer
        val headerView = TextView(this)
        headerView.text = "这是header"
        headerView.gravity = Gravity.CENTER
        headerView.setPadding(50, 50, 50, 50)
        baseAdapter.headerView = headerView
        baseAdapter.setFooterView(this, R.layout.adapter_two_footer)
        baseAdapter.footerView?.setOnClickListener { _ -> "你点击了footer".toast() }
        val list = TestData.createHomeList() //模拟请求数据
        val newList = HomeBaseBean.formatListData(list) //格式化数据
        baseAdapter.setListAndNotifyDataSetChanged(newList)

        //和子adapter的事件都调用会触发，注意自己的逻辑别和子adapter重复
        baseAdapter.setOnItemClickListener { _, _, _, vh, _, _ ->
            val absPosition = vh.commonPosition
            val listPosition = vh.listPosition
            Log.d(TAG, "全局点击事件，绝对位置: $absPosition，list的position：$listPosition")
        }
        baseAdapter.setOnItemViewLongClickListenerWithTag { _, _, _, vh, _, _, tag ->
            when (tag) {
                WaitPayOrderAdapter.TAG_CLICK_STATE -> {
                    "你长按了绝对位置：${vh.commonPosition}里的按钮".toast()
                }
            }
            true
        }
        rv.adapter = baseAdapter
    }

    fun startActivity(c: Class<*>) {
        startActivity(Intent(this, c))
    }

    companion object {
        const val TAG = "TwoActivity"
    }
}