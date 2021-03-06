package com.wang.container;

import android.annotation.TargetApi;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SimpleArrayMap;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.container.adapter.BaseContainerItemAdapter;
import com.wang.container.adapter.IContainerItemAdapter;
import com.wang.container.bean.IContainerBean;
import com.wang.container.bean.ItemAdapterPositionInfo;
import com.wang.container.helper.BaseListAdapterHelper;
import com.wang.container.holder.BaseViewHolder;
import com.wang.container.interfaces.IListAdapter;
import com.wang.container.interfaces.OnItemClickListener;
import com.wang.container.observer.IContainerObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 一个超级adapter可以添加其他adapter
 * <p>
 * 可以用在如：天猫首页、bilibili、今日头条、聊天列表页面
 * <p>
 * 核心思想：每个{@link BEAN}的item都当作一个adapter，为了复用和管理相同adapter，在子adapter传入了{@link BEAN}{@link IContainerItemAdapter#getCurrentBean}
 * <p>
 * 使用前提（都是无关紧要的，但也要看看）：
 * 1.bean必须继承{@link IContainerBean}
 * 2.子adapter必须是{@link IContainerItemAdapter}{@link BaseContainerItemAdapter}的子类
 * 3.子adapter的type必须在±10000之间{@link #TYPE_MAX}{@link #TYPE_MIN}
 * 4.如果是GridLayoutManager必须提前设置（在rv.setAdapter或addAdapter之前或手动调用{@link #changedLayoutManager}）
 * 5.有header时直接调用BaseContainerAdapter的{@link #notifyItemChanged}相关方法时需要+1（所有adapter的通病）（子adapter不受此影响）
 * 其他限制暂时没发现
 * <p>
 * https://blog.csdn.net/weimingjue/article/details/106468916
 */
public class BaseContainerAdapter<BEAN extends IContainerBean> extends RecyclerView.Adapter<BaseViewHolder> implements IListAdapter<BEAN, ViewDataBinding, OnItemClickListener<BEAN>> {
    protected final String TAG = getClass().getSimpleName();

    public static final int TYPE_MAX = 100000, TYPE_MIN = -100000;
    private static final int TYPE_MINUS = TYPE_MAX - TYPE_MIN;

    protected final int TYPE_HEADER = -1 * TYPE_MINUS + 1, TYPE_FOOTER = -1 * TYPE_MINUS + 2;//防止和adapter重复

    private final ItemAdapterPositionInfo mItemPositionCacheInfo = new ItemAdapterPositionInfo();

    protected MyAdaptersManager mAdaptersManager = new MyAdaptersManager();
    protected IContainerObserver mObservers = new IContainerObserver() {
        @Override
        public void notifyDataSetChanged() {
            BaseContainerAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void notifyItemChanged(int positionStart, int itemCount, @NonNull IContainerBean bean) {
            int newPosition = getAbsPosition(bean, positionStart);
            BaseContainerAdapter.this.notifyItemRangeChanged(newPosition, itemCount, null);
        }

        @Override
        public void notifyItemInserted(int positionStart, int itemCount, @NonNull IContainerBean bean) {
            int newPosition = getAbsPosition(bean, positionStart);
            BaseContainerAdapter.this.notifyItemRangeInserted(newPosition, itemCount);
        }

        @Override
        public void notifyItemMoved(int fromPosition, int toPosition, @NonNull IContainerBean bean) {
            int newPosition = getAbsPosition(bean, fromPosition);
            BaseContainerAdapter.this.notifyItemMoved(newPosition, newPosition + (toPosition - fromPosition));
        }

        @Override
        public void notifyItemRemoved(int positionStart, int itemCount, @NonNull IContainerBean bean) {
            int newPosition = getAbsPosition(bean, positionStart);
            BaseContainerAdapter.this.notifyItemRangeRemoved(newPosition, itemCount);
        }

        @Override
        public void dispatchItemClicked(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onClick(view);
            }
        }

        @Override
        public boolean dispatchItemLongClicked(View view) {
            if (mItemClickListener != null) {
                return mItemClickListener.onLongClick(view);
            }
            return false;
        }
    };

    protected RecyclerView mRecyclerView;
    protected GridLayoutManager mLayoutManager;

    @Nullable
    protected OnItemClickListener<BEAN> mItemClickListener;

    /**
     * list相关代码合并
     */
    private final BaseListAdapterHelper<BEAN> mHelper;

    private BaseViewHolder mBindTempHolder;

    /**
     * 注释同下
     */
    public BaseContainerAdapter() {
        this(null);
    }

    /**
     * 注意：如果是GridLayoutManager请提前设置好（在rv.setAdapter或addAdapter之前或手动调用）
     * 如果中途更换LayoutManager请调用{@link #changedLayoutManager}
     */
    public BaseContainerAdapter(@Nullable List<BEAN> list) {
        mHelper = new BaseListAdapterHelper<>(this, list);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER://每次create都会new一个布局，防止多次create导致崩溃
            case TYPE_FOOTER:
                return mHelper.onCreateHeaderFooterViewHolder(parent);
            default:
                return mAdaptersManager.getAdapter(viewType / TYPE_MINUS).createViewHolder(parent, viewType % TYPE_MINUS);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        mBindTempHolder = holder;
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                mHelper.onBindHeaderFooterViewHolder(holder, mHelper.mHeaderView);
                return;
            case TYPE_FOOTER:
                mHelper.onBindHeaderFooterViewHolder(holder, mHelper.mFooterView);
                return;
            default:
                ItemAdapterPositionInfo info = getItemPositionInfo(position);
                IContainerItemAdapter itemAdapter = info.mItemAdapter;
                //noinspection unchecked
                itemAdapter.setCurrentBean(getList().get(info.mListPosition));
                itemAdapter.bindViewHolder(holder, info.mItemPosition);
                //noinspection ConstantConditions,unchecked，置null便于检查错误
                itemAdapter.setCurrentBean(null);
                break;
        }
    }

    @Override
    public BaseViewHolder getBindTempViewHolder() {
        return mBindTempHolder;
    }

    @NonNull
    @Deprecated
    @Override
    @TargetApi(999)
    public BaseViewHolder<ViewDataBinding> onCreateListViewHolder(@NonNull ViewGroup parent) {
        //这两个方法是给其他adapter用的：https://github.com/weimingjue/BaseAdapter
        throw new RuntimeException("暂时无法实现，请勿调用");
    }

    @Deprecated
    @Override
    @TargetApi(999)
    public void onBindListViewHolder(@NonNull BaseViewHolder<ViewDataBinding> holder, int listPosition, BEAN bean) {
        throw new RuntimeException("暂时无法实现，请勿调用");
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderView() != null && position == 0) {
            return TYPE_HEADER;
        }
        if (getFooterView() != null && getItemCount() == position + 1) {
            return TYPE_FOOTER;
        }

        ItemAdapterPositionInfo info = getItemPositionInfo(position);
        IContainerItemAdapter itemAdapter = info.mItemAdapter;
        //noinspection unchecked
        itemAdapter.setCurrentBean(getList().get(info.mListPosition));
        int itemType = itemAdapter.getItemViewType(info.mItemPosition);
        //noinspection ConstantConditions,unchecked，置null便于检查错误
        itemAdapter.setCurrentBean(null);
        if (itemType < TYPE_MIN || itemType >= TYPE_MAX) {
            throw new RuntimeException("你的adapter" + itemAdapter.getClass() + "的type必须在" + TYPE_MIN + "~" + TYPE_MAX + "之间，type：" + itemType);
        }
        //根据mItemAdapters的position返回type，取的时候比较方便
        return mAdaptersManager.getPosition(itemAdapter.getClass()) * TYPE_MINUS + itemType;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (getHeaderView() != null) {
            count++;
        }
        if (getFooterView() != null) {
            count++;
        }
        for (BEAN bean : mHelper.mList) {
            IContainerItemAdapter itemAdapter = mAdaptersManager.getAdapter(bean.getBindAdapterClass());
            //noinspection unchecked 如果出现ClassCastException，请检查你list里的bean对象和adapter的bean是否一致
            itemAdapter.setCurrentBean(bean);
            count += itemAdapter.getItemCount();
            //noinspection ConstantConditions,unchecked，置null便于检查错误
            itemAdapter.setCurrentBean(null);
        }
        return count;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        checkLayoutManager();
    }

    protected void checkLayoutManager() {
        if (mRecyclerView == null) return;
        RecyclerView.LayoutManager manager = mRecyclerView.getLayoutManager();
        if ((mLayoutManager == null || mLayoutManager != manager) && manager instanceof GridLayoutManager) {
            changedLayoutManager((GridLayoutManager) manager);
        }
    }

    /**
     * 根据绝对position获取子adapter的相关信息
     * 已经做过{@link IContainerItemAdapter#setCurrentPositionInfo}
     *
     * @param position 绝对position
     */
    protected ItemAdapterPositionInfo getItemPositionInfo(int position) {
        if (getHeaderView() != null) {
            position--;//去掉header
        }
        //itemAdapter的position=0时的真实位置
        int itemStartPosition = 0;
        for (int i = 0; i < mHelper.mList.size(); i++) {
            BEAN bean = mHelper.mList.get(i);
            IContainerItemAdapter itemAdapter = mAdaptersManager.getAdapter(bean.getBindAdapterClass());
            //noinspection unchecked 如果出现ClassCastException，请检查你list里的bean对象和adapter的bean是否一致
            itemAdapter.setCurrentBean(bean);
            int itemCount = itemAdapter.getItemCount();
            //noinspection ConstantConditions,unchecked，置null便于检查错误
            itemAdapter.setCurrentBean(null);

            int nextStartPosition = itemStartPosition + itemCount;
            //下一个adapter的位置比position大说明当前type就在这个adapter中
            if (nextStartPosition > position) {
                mItemPositionCacheInfo.mListPosition = i;
                mItemPositionCacheInfo.mItemAdapter = itemAdapter;
                mItemPositionCacheInfo.mItemPosition = position - itemStartPosition;

                //当前状态
                mItemPositionCacheInfo.mAbsState = 0;
                if (position == 0) {
                    mItemPositionCacheInfo.mAbsState |= ItemAdapterPositionInfo.ABS_STATE_FIRST_LIST_POSITION;
                }
                if (position == mHelper.mList.size() - 1) {
                    mItemPositionCacheInfo.mAbsState |= ItemAdapterPositionInfo.ABS_STATE_LAST_LIST_POSITION;
                }
                if (getHeaderView() != null) {
                    mItemPositionCacheInfo.mAbsState |= ItemAdapterPositionInfo.ABS_STATE_HAS_HEADER;
                }
                if (getFooterView() != null) {
                    mItemPositionCacheInfo.mAbsState |= ItemAdapterPositionInfo.ABS_STATE_HAS_FOOTER;
                }
                //设置position信息
                mItemPositionCacheInfo.mItemAdapter.setCurrentPositionInfo(mItemPositionCacheInfo);
                return mItemPositionCacheInfo;
            } else {
                //循环相加
                itemStartPosition = nextStartPosition;
            }
        }
        throw new RuntimeException("没有取到对应的type,可能你没有(及时)刷新adapter");
    }

    /**
     * position,adapter,class唯一并且可以互相取值
     */
    protected class MyAdaptersManager {
        protected SimpleArrayMap<Class<? extends IContainerItemAdapter>, Integer> mMap = new SimpleArrayMap<>(8);
        protected ArrayList<IContainerItemAdapter> mList = new ArrayList<>(8);

        protected void addAdapter(IContainerItemAdapter... adapters) {
            for (IContainerItemAdapter adapter : adapters) {
                adapter.registerDataSetObserver(mObservers);
                if (!mMap.containsKey(adapter.getClass())) {
                    mList.add(adapter);
                    mMap.put(adapter.getClass(), mList.size() - 1);
                }
            }
        }

        protected void addAdapter(List<? extends IContainerItemAdapter> adapters) {
            for (IContainerItemAdapter adapter : adapters) {
                adapter.attachContainer(BaseContainerAdapter.this);
                adapter.registerDataSetObserver(mObservers);
                if (!mMap.containsKey(adapter.getClass())) {
                    mList.add(adapter);
                    mMap.put(adapter.getClass(), mList.size() - 1);
                }
            }
        }

        /**
         * @throws RuntimeException 见{@link #getPosition}
         */
        protected IContainerItemAdapter getAdapter(int position) {
            if (position < 0 || position >= mList.size()) {
                throw new RuntimeException("缺少对应的adapter，adapter数量：" + mList.size() + "，当前index：" + position);
            }
            return mList.get(position);
        }

        /**
         * @throws RuntimeException 见{@link #getPosition}
         */
        protected IContainerItemAdapter getAdapter(Class<? extends IContainerItemAdapter> cls) {
            Integer index = mMap.get(cls);
            if (index == null) {
                //
                throw new RuntimeException("缺少对应的adapter：" + cls);
            }
            return mList.get(index);
        }

        /**
         * @throws NullPointerException 一般是数据变化没有(及时)刷新adapter导致的
         */
        @SuppressWarnings("ConstantConditions")
        protected int getPosition(Class<? extends IContainerItemAdapter> cls) {
            return mMap.get(cls);
        }

        protected void remove(int position) {
            IContainerItemAdapter remove = mList.remove(position);
            mMap.remove(remove.getClass());
        }

        protected void remove(Class<? extends IContainerItemAdapter> cls) {
            Integer remove = mMap.remove(cls);
            if (remove != null) {
                mList.remove((int) remove);
            }
        }

        protected void clear() {
            mList.clear();
            mMap.clear();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 以下是可调用方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 添加adapter.重复则不会被添加,必须先删除
     * 当然可以预先添加用不到的adapter
     */
    public BaseContainerAdapter<BEAN> addAdapter(@NonNull IContainerItemAdapter... adapters) {
        return addAdapter(Arrays.asList(adapters));
    }

    /**
     * 添加adapter.重复则不会被添加,必须先删除
     * 当然可以预先添加用不到的adapter
     */
    public BaseContainerAdapter<BEAN> addAdapter(@NonNull List<? extends IContainerItemAdapter> adapters) {
        mAdaptersManager.addAdapter(adapters);
        checkLayoutManager();
        notifyDataSetChanged();
        return this;
    }

    /**
     * 返回所有的adapter
     */
    public ArrayList<IContainerItemAdapter> getAdapters() {
        return new ArrayList<>(mAdaptersManager.mList);//为了安全起见，返回一个新的List
    }

    @NonNull
    public List<BEAN> getList() {
        return mHelper.mList;
    }

    /**
     * header、footer不复用，点击事件自己写
     *
     * @param view null表示删除，view的parent为FrameLayout，默认match、wrap
     */
    public void setHeaderView(@Nullable View view) {
        mHelper.setHeaderView(view);
    }

    @Nullable
    @Override
    public View getHeaderView() {
        return mHelper.mHeaderView;
    }

    /**
     * header、footer不复用，点击事件自己写
     *
     * @param view null表示删除，view的parent为FrameLayout，默认match、wrap
     */
    public void setFooterView(@Nullable View view) {
        mHelper.setFooterView(view);
    }

    @Nullable
    @Override
    public View getFooterView() {
        return mHelper.mFooterView;
    }

    /**
     * 这个回调和子adapter的事件回调都会被调用（这里先调，子adapter后调）
     * <p>
     * 注意同样逻辑别写2遍
     * 如果没有收到回调请检查你的adapter有没有对{@link RecyclerView.ViewHolder#itemView}设置了点击事件
     * <p>
     * position为相对position（绝对的没啥用处），想获得绝对位置{@link #getAbsPosition}
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener<BEAN> listener) {
        mItemClickListener = listener;
    }

    @Nullable
    @Override
    public OnItemClickListener<BEAN> getOnItemClickListener() {
        return mItemClickListener;
    }

    /**
     * 根据bean对象和adapter的相对位置获取绝对位置
     *
     * @param itemAdapterPosition 相对potion
     */
    public int getAbsPosition(IContainerBean bean, int itemAdapterPosition) {
        int position = itemAdapterPosition;
        for (BEAN listBean : mHelper.mList) {
            if (listBean == bean) {
                if (getHeaderView() != null) {
                    position++;//加上header
                }
                return position;
            } else {
                IContainerItemAdapter itemAdapter = mAdaptersManager.getAdapter(bean.getBindAdapterClass());
                //noinspection unchecked 如果出现ClassCastException，请检查你list里的bean对象和adapter的bean是否一致
                itemAdapter.setCurrentBean(listBean);
                position += itemAdapter.getItemCount();
                //noinspection ConstantConditions,unchecked，置null便于检查错误
                itemAdapter.setCurrentBean(null);
            }
        }
        throw new RuntimeException("在list中没有找到传入的bean对象" + bean);
    }

    /**
     * 根据绝对position获取对应adapter的额外信息
     *
     * @return 这个对象是复用的，一次性消费
     */
    @MainThread
    public ItemAdapterPositionInfo getItemAdapterPositionInfo(int absPosition) {
        return getItemPositionInfo(absPosition);
    }

    /**
     * 根据bean和相对position获取对应adapter的额外信息
     *
     * @param itemAdapterPosition 相对potion
     * @return 这个对象是复用的，一次性消费
     */
    @MainThread
    public ItemAdapterPositionInfo getItemAdapterPositionInfo(IContainerBean bean, int itemAdapterPosition) {
        return getItemAdapterPositionInfo(getAbsPosition(bean, itemAdapterPosition));
    }

    /**
     * 删除指定adapter
     * 别忘了同步修改list数据和{@link #notifyDataSetChanged}，不然会直接崩溃的哦
     *
     * @param adapterPosition 按添加顺序第几个
     */
    public BaseContainerAdapter<BEAN> removeAdapter(int adapterPosition) {
        mAdaptersManager.remove(adapterPosition);
        return this;
    }

    /**
     * 删除指定adapter
     * 别忘了同步修改list数据和{@link #notifyDataSetChanged}，不然会直接崩溃的哦
     *
     * @param adapterClass 哪个adapter
     */
    public BaseContainerAdapter<BEAN> removeAdapter(Class<? extends IContainerItemAdapter> adapterClass) {
        mAdaptersManager.remove(adapterClass);
        return this;
    }

    /**
     * 清空adapter
     * 别忘了同步修改list数据和{@link #notifyDataSetChanged}，不然会直接崩溃的哦
     */
    public BaseContainerAdapter<BEAN> removeAllAdapter() {
        mAdaptersManager.clear();
        return this;
    }

    /**
     * 把rv的LayoutManager改成其他的GridLayoutManager时.此方法理论上没啥用
     */
    public void changedLayoutManager(GridLayoutManager manager) {
        if (manager == null) return;
        mLayoutManager = manager;
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (getHeaderView() != null && position == 0) {
                    return mLayoutManager.getSpanCount();
                } else if (getFooterView() != null && getItemCount() == position + 1) {
                    return mLayoutManager.getSpanCount();
                }
                ItemAdapterPositionInfo info = getItemPositionInfo(position);
                IContainerItemAdapter itemAdapter = info.mItemAdapter;
                //noinspection unchecked
                itemAdapter.setCurrentBean(getList().get(info.mListPosition));
                int spanSize = itemAdapter.getSpanSize(info.mItemPosition);
                //noinspection ConstantConditions,unchecked，置null便于检查错误
                itemAdapter.setCurrentBean(null);
                return spanSize;
            }
        });
    }
}