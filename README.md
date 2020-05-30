# 一个通过add其他adapter的超级容器，无论多么复杂的列表样式均可解耦成一个一个的adapter

## 详细示例见本项目app下的MainActivity
容器非常简单
```
        BaseContainerAdapter baseAdapter = new BaseContainerAdapter();
        mRv.setAdapter(baseAdapter.addAdapter(new TextAdapter()));
        //...
        baseAdapter.setListAndNotifyDataSetChanged(list);
```
子adapter基本和RecyclerView一致
（由于adapter是复用的，所以增加了getCurrentBean()来确定当前属于哪条数据）
```public class TextAdapter extends BaseContainerItemAdapter<RecyclerView.ViewHolder, TextBean> {

       @Override
       protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
           TextView tv = (TextView) holder.itemView;
           tv.setText("这是文字：" + getCurrentBean().textInfo.text);
       }

       @Override
       protected RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType, LayoutInflater inflater) {
           return new RecyclerView.ViewHolder(new AppCompatTextView(parent.getContext())) {};
       }
   }
```
每个adapter都自带点击事件
```
        itemAdapter.setOnItemClickListener(new OnItemClickListener<TextBean>() {
            @Override
            protected void onItemClick(View view, int position) {
                TextBean bean = getCurrentBean();
                Toast.makeText(MyApplication.getContext(), "您点击了：" + bean.textInfo.text, Toast.LENGTH_SHORT).show();
            }
        });
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
`implementation（或api） 'com.github.weimingjue:BaseContainerAdapter:1.00'`
