# 一个通过add其他adapter的超级容器，无论多么复杂的列表样式均可解耦成一个一个的adapter

## 详细示例见本项目app下的MainActivity

首先要明白adapter也是复用的，所以才有getCurrentBean()这种操作（没加在方法上是为了更简单更亲和于RecyclerView的adapter）

容器非常简单
```
mRv.setLayoutManager(new LinearLayoutManager(this));//如果是GridLayoutManager需要提前设置好，Linear随意
BaseContainerAdapter baseAdapter = new BaseContainerAdapter();
mRv.setAdapter(baseAdapter.addAdapter(new TextAdapter()));
//...
baseAdapter.setListAndNotifyDataSetChanged(list);
```
子adapter基本和RecyclerView一致（一个条目的话，可以不需要layoutRes）
```
public class WaitPayOrderAdapter extends OneContainerItemAdapter<AdapterMsgWaitPayOrderBinding, OrderBean> {

    @Override
    protected void onBindChildViewHolder(@NonNull BaseViewHolder<AdapterMsgWaitPayOrderBinding> holder, OrderBean bean) {
        String text = "列表状态：";
        ItemAdapterPositionInfo info = getCurrentPositionInfo();
        if (info.isFirst()) {
            text += "整个列表第一个";
        }
        if (info.isLast()) {
            text += "整个列表最后一个";
        }
        if (info.isCenter()) {
            text += "列表中间";
        }
        holder.getBinding().btState.setText(text);
    }
}
```
每个adapter都自带点击事件
```
itemAdapter.setOnItemClickListener(new OnItemClickListener<TextBean>() {
    @Override
    public void onItemClick(@NonNull View view, int position) {
        TextBean bean = getCurrentBean();
        Toast.makeText(MyApplication.getContext(), "您点击了：" + bean.textInfo.text, Toast.LENGTH_SHORT).show();
    }
});
```
容器也可以设置点击事件
（和子adapter的事件都调用会触发，注意自己的逻辑别和子adapter重复）
```
baseAdapter.setOnItemClickListener(new OnItemClickListener<BaseMsgBean>() {
    @Override
    public void onItemClick(View view, int position) {
        int absPosition = baseAdapter.getAbsPosition(getCurrentBean(), position);
        Toast.makeText(MainActivity.this, "Base的点击事件，绝对位置：" + absPosition, Toast.LENGTH_SHORT).show();
    }
});
```
容器可以设置header、footer（子adapter暂不支持）
（和子adapter的事件都调用会触发，注意自己的逻辑别和子adapter重复）
```
baseAdapter.setHeaderView(headerView);
baseAdapter.setFooterView(this, R.layout.adapter_main_footer);//根布局可以使用height、layout_margin、layout_gravity相关属性
baseAdapter.getFooterView().setOnClickListener(v -> ToastUtils.toast("你点击了footer"));
```
## 拓展功能
既然是adapter当然也可以有多条数据和多条目了
```
public class TextAdapter extends BaseContainerItemAdapter<BaseViewHolder, TextBean> {

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        TextView tv = (TextView) holder.itemView;
        tv.setText("这是文字：" + getCurrentBean().textInfo.text);
        //ItemAdapterPositionInfo info = getCurrentPositionInfo();
        //TextBean currentBean = (TextBean) getContainerAdapter().get(info.mListPosition);//和getCurrentBean()一样的
    }

    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder(new AppCompatTextView(parent.getContext()));
    }

    /**
     * @return 基于当前bean的count个数，和{@link #onBindViewHolder(BaseViewHolder, int)}的position对应
     */
    @Override
    public int getItemCount() {
        TextBean bean = getCurrentBean();
        return bean.textInfo.moreTextData.size() + 1;
    }

    /**
     * @param position 相对的position
     * @return 相对的type，只要在-10000到10000之间即可，不会影响其他adapter
     */
    @Override
    public int getItemViewType(int position) {
        TextBean bean = getCurrentBean();
        return position == 0 ? 0 : 1;
    }
}
```
rv的动画效果
```
adapter.setOnItemClickListener(new OnItemClickListener<TextBean>() {
    @Override
    public void onItemClick(@NonNull View view, int position) {
        TextBean bean = getCurrentBean();
        getContainerAdapter().getList().remove(bean);//删除这个bean
        notifyItemRemoved(position, bean);//刷新删除数据
    }
});
```
如果是GridLayoutManager，也有getSpanSize
```
public class TextAdapter extends BaseContainerItemAdapter<BaseViewHolder, TextBean> {

    /**
     * 不需要设置{@link GridLayoutManager#setSpanSizeLookup}
     * 默认已经设置过了{@link BaseContainerAdapter#changedLayoutManager}
     */
    @Override
    public int getSpanSize(int position) {
        return 10;
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

### 3.0.3以后方法变更：
```
OnItemClickListener.getCurrentBean()>OnItemClickListener.getCurrentBean(view)（view为回调的view）
OnItemClickListener.getCurrentViewHolder()>OnItemClickListener.getViewHolder(view)
```
### 特殊情况
如果真的不想加混淆，则adapter的构造里传入layoutRes即可
```
public MyAdapter() {
    super(R.layout.adapter_main_list, null);
}
```

## 导入方式
你的build.gradle要有jitpack.io，大致如下
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
`implementation（或api） 'com.github.weimingjue:BaseContainerAdapter:3.1.0'`

混淆要求：
```
# 框架特殊要求
# 根据泛型获取res资源需要
-keep class * extends androidx.databinding.ViewDataBinding
```
