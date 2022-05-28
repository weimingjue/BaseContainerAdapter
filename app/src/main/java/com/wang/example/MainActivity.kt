package com.wang.example

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wang.container.BaseContainerAdapter
import com.wang.example.msg.adapter.*
import com.wang.example.msg.bean.BaseMsgBean
import com.wang.example.msg.bean.BaseMsgBean.Companion.formatListData
import com.wang.example.msg.bean.TestData.createMsgList
import com.wang.example.utils.toast

class MainActivity : AppCompatActivity() {
    private var mRv: RecyclerView? = null
    private var mBaseAdapter: BaseContainerAdapter<*>? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.tv_msg).setOnClickListener { createMsg() }
        findViewById<View>(R.id.tv_other).setOnClickListener { "暂时没精力写".toast() }
        findViewById<View>(R.id.tv_reverse).setOnClickListener {
            mBaseAdapter?.let {
                it.list.reverse()
                it.notifyDataSetChanged()
            }
        }
        findViewById<View>(R.id.tv_delete).setOnClickListener { _ ->
            mBaseAdapter?.let { it ->
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
        mRv = findViewById(R.id.rv_main)
        mRv?.layoutManager = LinearLayoutManager(this)
    }

    private fun createMsg() {
        val baseAdapter = BaseContainerAdapter<BaseMsgBean>()
        baseAdapter.addAdapter(
            TextAdapter(), ImgAdapter(), WaitPayOrderAdapter(),
            PaySuccessOrderAdapter(), UnsupportedAdapter()
        )

        //header、footer
        val headerView = TextView(this)
        headerView.text = "这是header"
        headerView.gravity = Gravity.CENTER
        headerView.setPadding(50, 50, 50, 50)
        baseAdapter.headerView = headerView
        baseAdapter.setFooterView(this, R.layout.adapter_main_footer)
        baseAdapter.footerView!!.setOnClickListener { v: View? -> "你点击了footer".toast() }
        val list = createMsgList() //模拟请求数据
        val newList = formatListData(list) //格式化数据
        baseAdapter.setListAndNotifyDataSetChanged(newList)

        //和子adapter的事件都调用会触发，注意自己的逻辑别和子adapter重复
        baseAdapter.setOnItemClickListener { _, _, _, vh, _, _ ->
            val absPosition = vh.commonPosition
            val listPosition = vh.listPosition
            //                absPosition = baseAdapter.getAbsPosition(getCurrentBean(view), position);//一个效果
            Log.d(TAG, "全局点击事件，绝对位置: $absPosition，list的position：$listPosition")
        }
        mRv?.adapter = baseAdapter
        mBaseAdapter = baseAdapter
    }

    fun startActivity(c: Class<*>) {
        startActivity(Intent(this, c))
    }

    companion object {
        const val TAG = "MainActivity"
    }
}