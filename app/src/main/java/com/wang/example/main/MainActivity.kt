package com.wang.example.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wang.container.BaseContainerAdapter
import com.wang.example.R
import com.wang.example.main.adapter.*
import com.wang.example.main.bean.BaseMsgBean
import com.wang.example.main.bean.TestData
import com.wang.example.two.TwoActivity
import com.wang.example.utils.toast

class MainActivity : AppCompatActivity() {
    private var rv: RecyclerView? = null
    private var baseAdapter: BaseContainerAdapter<*>? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.tv_msg).setOnClickListener { createMsg() }
        findViewById<View>(R.id.tv_other).setOnClickListener { startActivity(TwoActivity::class.java) }
        findViewById<View>(R.id.tv_reverse).setOnClickListener {
            baseAdapter?.let {
                it.list.reverse()
                it.notifyDataSetChanged()
            }
        }
        findViewById<View>(R.id.tv_delete).setOnClickListener { _ ->
            baseAdapter?.let { it ->
                val random = (Math.random() * it.listSize().coerceAtMost(5)).toInt()
                if (random < it.listSize()) {
                    it.list.removeAt(random)
                    //这里需要注意，header的存在
                    it.notifyItemRemoved(random + it.headerViewCount)
                    "你删除了list position：$random".toast()

                    //remove动画不会改变第一条和最后一条的状态，所以带header、footer提示的话也要同步刷新
                    it.notifyItemChanged(it.headerViewCount)
                    it.notifyItemChanged(it.listSize() + it.headerViewCount - 1)
                }
            }
        }
        rv = findViewById(R.id.rv_main)
        rv?.layoutManager = LinearLayoutManager(this)
    }

    private fun createMsg() {
        val ba = BaseContainerAdapter<BaseMsgBean>()
        ba.addAdapter(
            TextAdapter(), ImgAdapter(), WaitPayOrderAdapter(),
            PaySuccessOrderAdapter(), UnsupportedAdapter()
        )

        //header、footer
        val headerView = TextView(this)
        headerView.text = "这是header"
        headerView.gravity = Gravity.CENTER
        headerView.setPadding(50, 50, 50, 50)
        ba.headerView = headerView
        ba.setFooterView(this, R.layout.adapter_main_footer)
        ba.footerView?.setOnClickListener { _ -> "你点击了footer".toast() }
        val list = TestData.createMsgList() //模拟请求数据
        val newList = BaseMsgBean.formatListData(list) //格式化数据
        ba.setListAndNotify(newList)

        rv?.adapter = ba
        baseAdapter = ba
    }

    fun startActivity(c: Class<*>) {
        startActivity(Intent(this, c))
    }

    companion object {
        const val TAG = "MainActivity"
    }
}