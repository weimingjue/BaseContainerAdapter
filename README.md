# 一个通过add其他adapter的超级容器，无论多么复杂的列表样式均可解耦成一个一个的adapter

## 详细示例见本项目app下的MainActivity

首先要明白adapter也是复用的，所以才有currentBean这个参数（由于贴近RecyclerView会导致api难以理解，所以直接放在回调里了）

容器非常简单
```
rv?.layoutManager = LinearLayoutManager(this)//如果是GridLayoutManager需要提前设置好，Linear随意
BaseContainerAdapter baseAdapter = BaseContainerAdapter()
baseAdapter.addAdapter(TextAdapter())
rv?.adapter = baseAdapter
//...
baseAdapter.setListAndNotifyDataSetChanged(list)
```
子adapter基本和RecyclerView一致（一个条目的话，可以不需要layoutRes）
```
class WaitPayOrderAdapter : OneContainerItemAdapter<AdapterMsgWaitPayOrderBinding, OrderBean>() {
    override fun onBindChildViewHolder(
        holder: BaseViewHolder<AdapterMsgWaitPayOrderBinding>,
        currentBean: OrderBean
    ) {
        var text = "列表状态："
        val info = getCurrentPositionInfo(currentBean)
        if (info.isFirst) {
            text += "整个列表第一个"
        }
        if (info.isLast) {
            text += "整个列表最后一个"
        }
        if (info.isCenter) {
            text += "列表中间"
        }
        holder.vb.btState.text = text
        holder.vb.tvOrderNo.text = "订单号：${currentBean.orderInfo.orderNo}"
        holder.vb.tvOrderName.text = "订单名称：${currentBean.orderInfo.orderName}"
        addItemViewClickWithTag(holder.vb.btState, holder, TAG_CLICK_STATE)
    }
}
```
每个adapter都自带点击事件
```
//单点击
itemAdapter.setOnItemClickListener { _, _, _, vh, _, _ ->
    "您点击了待支付条目，绝对位置：${vh.commonPosition}".toast()
}
//item里的view点击建议使用tag方式
itemAdapter.setOnItemViewClickListenerWithTag { _, _, _, vh, _, _, tag ->
    when (tag) {
        TAG_CLICK_STATE -> {
            "您点击了列表按钮，绝对位置：${vh.commonPosition}".toast()
        }
    }
}
//所有点击长按等事件的回调合集
itemAdapter.setOnItemClickListener(object : OnItemClickListener<BaseMsgBean> {
    override fun onItemClick...
    override fun onItemLongClick...
    override fun onItemViewClickWithTag...
    override fun onItemViewLongClickWithTag...
})
```
容器也可以设置点击事件
（和子adapter的事件都调用会触发，注意自己的逻辑别和子adapter重复）
```
baseAdapter.setOnItemClickListener...//和子保持一致
```
容器可以设置header、footer（子adapter暂不支持）
```
baseAdapter.headerView = headerView
baseAdapter.setFooterView(this, R.layout.adapter_main_footer)//根布局可以使用height、layout_margin、layout_gravity相关属性
baseAdapter.footerView?.setOnClickListener { _ -> "你点击了footer".toast() }
```
## 拓展功能
既然是adapter当然也可以有多条数据和多条目了
```
class HomeGoodsAdapter : BaseContainerItemAdapter<HomeGoodsBean>() {
    private val typeHeader = 1
    private val typeBody = -1

    override fun getItemCount(currentBean: HomeGoodsBean): Int {
        return currentBean.goodsInfo.goodsResList.size + 1
    }

    override fun getItemViewType(currentBean: HomeGoodsBean, relativePosition: Int): Int {
        if (relativePosition == 0) {
            return typeHeader
        }
        return typeBody
    }

    override fun getSpanSize(currentBean: HomeGoodsBean, relativePosition: Int): Int {
        return when (getItemViewType(currentBean, relativePosition)) {
            typeHeader -> 4
            else -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        when (viewType) {
            typeHeader -> {
                val tv = AppCompatTextView(parent.context)
                return BaseViewHolder<ViewBinding>(tv)
            }
            else -> {
                return BaseViewHolder(AdapterHomeGoodsBinding.inflate( LayoutInflater.from(parent.context),  parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, currentBean: HomeGoodsBean, relativePosition: Int) {
        when (getItemViewType(currentBean, relativePosition)) {
            typeHeader -> {
                val tv: TextView = holder.itemView as TextView
                tv.text = "这是商品标题：" + currentBean.goodsInfo.title
            }
            else -> {
                holder as BaseViewHolder<AdapterHomeGoodsBinding>
                val goodsIndex = relativePosition - 1
                holder.vb.ivGoods.setImageResource(currentBean.goodsInfo.goodsResList[goodsIndex])
            }
        }
    }

    init {
        setOnItemClickListener { _, relativePosition, bean, vh, _, _ ->
            when (getItemViewType(bean, relativePosition)) {
                typeHeader -> {
                    "您点击了商品文字：${bean.goodsInfo.title}，绝对位置：${vh.commonPosition}，相对位置：$relativePosition".toast()
                }
                else -> {
                    "您点击了商品图片，绝对位置：${vh.commonPosition}，图片相对位置：${relativePosition - 1}".toast()
                }
            }
        }
    }
}
```
支持rv自带的效果
```
itemAdapter.setOnItemClickListener { _, _, currentBean, _, _, _ ->
        getContainerAdapter().getList().remove(bean);//删除这个bean
        notifyItemRemoved(position, bean);//刷新删除数据
}
```
如果是GridLayoutManager，也有getSpanSize
```
class HomeGoodsAdapter : BaseContainerItemAdapter<HomeGoodsBean>() {
    /**
     * 不需要设置{@link GridLayoutManager#setSpanSizeLookup}
     * 默认已经设置过了{@link BaseContainerAdapter#changedLayoutManager}
     */
    override fun getSpanSize(currentBean: HomeGoodsBean, relativePosition: Int): Int {
        return when (getItemViewType(currentBean, relativePosition)) {
            typeHeader -> 4
            else -> 1
        }
    }
}
```
想知道在整个RecyclerView的状态？也很简单
```
public class TextAdapter extends BaseContainerItemAdapter<BaseViewHolder, TextBean> {

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        ItemAdapterPositionInfo info = getCurrentPositionInfo();//详见ItemAdapterPositionInfo类
        int absState = info.mAbsState;
        if (info.isFirst()) {
            text += "，整个列表第一个";
        }
        if (info.isLast()) {
            text += "，整个列表最后一个";
        }
    }
}
//考虑到性能问题只能在{@link #bindViewHolder}{@link #getItemViewType}{@link #getSpanSize}三个地方使用
//
//当然也可以主动获取了（这个操作会遍历2遍数据，千条数据还是可以随便高频调用的）
ItemAdapterPositionInfo info = getContainerAdapter().getItemAdapterPositionInfo(getCurrentBean(), position);
```

### 版本变更
**3.0.3以后方法变更：**
```
OnItemClickListener.getCurrentBean()>OnItemClickListener.getCurrentBean(view)（view为回调的view）
OnItemClickListener.getCurrentViewHolder()>OnItemClickListener.getViewHolder(view)
```
**3.1.1升级请注意：**

由于getCurrentBean可能会被错误的滥用，所以错误使用时（延时调用或其他乱用）会抛出NullPointerException，升级版本时请自行重新测试一遍

### 普通Adapter见
 [一个极简化的adapter](https://github.com/weimingjue/BaseAdapter)
```
public MyAdapter() {
    super(R.layout.adapter_main_list, null);
}
```

## 导入方式
你的build.gradle要有jitpack.io，大致如下：
```
allprojects {
    repositories {
        maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
        maven { url 'https://jitpack.io' }
        google()
        jcenter()
    }
}
```
AndroidX导入：
`implementation（或api） 'com.github.weimingjue:BaseContainerAdapter:3.2.0'`

混淆要求：
加 -keep class * extends androidx.databinding.ViewBinding 可能会快一点
不加也没啥影响