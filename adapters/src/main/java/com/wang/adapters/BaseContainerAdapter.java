package com.wang.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SimpleArrayMap;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.adapters.adapter.BaseContainerItemAdapter;
import com.wang.adapters.adapter.IContainerItemAdapter;
import com.wang.adapters.bean.IContainerBean;
import com.wang.adapters.observer.IContainerObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个超级adapter可以添加其他adapter
 * <p>
 * 可以用在如：天猫首页、bilibili、今日头条、聊天列表页面
 * <p>
 * 核心思想：每个{@link BEAN}的item都当作一个adapter，为了复用和管理相同adapter，在子adapter传入了{@link BEAN}{@link IContainerItemAdapter#getCurrentBean}
 * <p>
 * 限制条件：
 * 1.bean必须继承{@link IContainerBean}
 * 2.子adapter必须是{@link IContainerItemAdapter},{@link BaseContainerItemAdapter}的子类
 * <p>
 * 3.子adapter的type必须在±10000之间{@link #TYPE_MAX}{@link #TYPE_MIN}
 * 4.如果是GridLayoutManager必须提前设置（在rv.setAdapter或addAdapter之前或手动调用）
 * 其他限制暂时没发现
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class BaseContainerAdapter<BEAN extends IContainerBean> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final String TAG = getClass().getSimpleName();

    public static final int TYPE_MAX = 100000, TYPE_MIN = -100000;
    private static final int TYPE_MINUS = TYPE_MAX - TYPE_MIN;

    protected MyItemAdapter mItemAdapters = new MyItemAdapter();
    protected IContainerObserver mObservers = new IContainerObserver() {
        @Override
        public void notifyDataSetChanged() {
            BaseContainerAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void notifyItemChanged(int position, IContainerBean bean) {
            notifyItemChanged(position, 1, bean);
        }

        @Override
        public void notifyItemChanged(int positionStart, int itemCount, IContainerBean bean) {
            int newPosition = getBaseAdapterPosition(bean, positionStart);
            BaseContainerAdapter.this.notifyItemRangeChanged(newPosition, itemCount, null);
        }

        @Override
        public void notifyItemInserted(int position, IContainerBean bean) {
            notifyItemInserted(position, 1, bean);
        }

        @Override
        public void notifyItemInserted(int positionStart, int itemCount, IContainerBean bean) {
            int newPosition = getBaseAdapterPosition(bean, positionStart);
            BaseContainerAdapter.this.notifyItemRangeInserted(newPosition, itemCount);
        }

        @Override
        public void notifyItemMoved(int fromPosition, int toPosition, IContainerBean bean) {
            int newPosition = getBaseAdapterPosition(bean, fromPosition);
            BaseContainerAdapter.this.notifyItemMoved(newPosition, newPosition + (toPosition - fromPosition));
        }

        @Override
        public void notifyItemRemoved(int position, IContainerBean bean) {
            notifyItemRemoved(position, 1, bean);
        }

        @Override
        public void notifyItemRemoved(int positionStart, int itemCount, IContainerBean bean) {
            int newPosition = getBaseAdapterPosition(bean, positionStart);
            BaseContainerAdapter.this.notifyItemRangeRemoved(newPosition, itemCount);
        }
    };

    protected RecyclerView mRecyclerView;
    protected GridLayoutManager mLayoutManager;

    @NonNull
    private List<BEAN> mList;

    /**
     * 子adapter的信息,具体见{@link MyItemInfo#refreshItemInfo}
     */
    protected MyItemInfo mItemInfo = new MyItemInfo();

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
        mList = list == null ? new ArrayList<BEAN>() : list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mItemAdapters.getAdapter(viewType / TYPE_MINUS).createViewHolder(parent, viewType % TYPE_MINUS, LayoutInflater.from(parent.getContext()));
    }

    @SuppressWarnings("unchecked")//未检查警告,此处忽略
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        mItemInfo.refreshItemInfo(position);
        IContainerItemAdapter itemAdapter = mItemInfo.mItemAdapter;
        itemAdapter.setCurrentBean(mList.get(mItemInfo.mListPosition));
        itemAdapter.bindViewHolder(holder, mItemInfo.mItemPosition);
    }

    @SuppressWarnings("unchecked")//未检查警告,此处忽略
    @Override
    public int getItemViewType(int position) {
        mItemInfo.refreshItemInfo(position);
        IContainerItemAdapter itemAdapter = mItemInfo.mItemAdapter;
        itemAdapter.setCurrentBean(mList.get(mItemInfo.mListPosition));
        int itemType = itemAdapter.getItemViewType(mItemInfo.mItemPosition);
        if (itemType < TYPE_MIN || itemType >= TYPE_MAX) {
            throw new RuntimeException("你的adapter" + itemAdapter.getClass() + "的type必须在" + TYPE_MIN + "~" + TYPE_MAX + "之间，");
        }
        //根据mItemAdapters的position返回type，取的时候比较方便
        return mItemAdapters.getPosition(itemAdapter.getClass()) * TYPE_MINUS + itemType;
    }

    @SuppressWarnings("unchecked")//未检查警告,此处忽略
    @Override
    public int getItemCount() {
        int count = 0;
        for (BEAN bean : mList) {
            IContainerItemAdapter itemAdapter = mItemAdapters.getAdapter(bean.getItemAdapterClass());
            itemAdapter.setCurrentBean(bean);
            count += itemAdapter.getItemCount();
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
     * 根据bean对象和adapter的相对位置获取绝对位置
     */
    @SuppressWarnings("unchecked")//未检查警告,此处忽略
    protected int getBaseAdapterPosition(IContainerBean bean, int itemAdapterPosition) {
        int position = itemAdapterPosition;
        for (BEAN listBean : mList) {
            if (listBean == bean) {
                return position;
            } else {
                IContainerItemAdapter itemAdapter = mItemAdapters.getAdapter(bean.getItemAdapterClass());
                itemAdapter.setCurrentBean(listBean);
                position += itemAdapter.getItemCount();
            }
        }
        throw new RuntimeException("在list中没有找到传入的bean对象" + bean);
    }

    protected class MyItemInfo {
        /**
         * 使用之前请调用{@link #refreshItemInfo}
         * list的position,子adapter的所需要的相对position
         */
        protected int mListPosition, mItemPosition;
        protected IContainerItemAdapter mItemAdapter;

        /**
         * 根据超级adapter的position返回子adapter的信息
         */
        @SuppressWarnings("unchecked")//未检查警告,此处忽略
        protected MyItemInfo refreshItemInfo(int position) {
            //itemAdapter的position=0时的真实位置
            int itemStartPosition = 0;
            for (int i = 0; i < mList.size(); i++) {
                BEAN bean = mList.get(i);
                IContainerItemAdapter itemAdapter = mItemAdapters.getAdapter(bean.getItemAdapterClass());
                itemAdapter.setCurrentBean(bean);
                int itemCount = itemAdapter.getItemCount();
                int nextStartPosition = itemStartPosition + itemCount;
                //下一个adapter的位置比position大说明当前type就在这个adapter中
                if (nextStartPosition > position) {
                    mListPosition = i;
                    mItemAdapter = itemAdapter;
                    mItemPosition = position - itemStartPosition;
                    return this;
                } else {
                    //循环相加
                    itemStartPosition = nextStartPosition;
                }
            }
            throw new RuntimeException("没有取到对应的type,可能你没有(及时)刷新adapter");
        }
    }

    /**
     * position,adapter,class唯一并且可以互相取值
     */
    protected class MyItemAdapter {
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
                throw new RuntimeException("缺少对应的adapter，adapter数量：" + mList.size() + "，当前数量：" + position);
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
        mItemAdapters.addAdapter(adapters);
        checkLayoutManager();
        notifyDataSetChanged();
        return this;
    }

    /**
     * 添加adapter.重复则不会被添加,必须先删除
     * 当然可以预先添加用不到的adapter
     */
    public BaseContainerAdapter<BEAN> addAdapter(@NonNull List<? extends IContainerItemAdapter<? extends RecyclerView.ViewHolder, BEAN>> adapters) {
        mItemAdapters.addAdapter(adapters);
        checkLayoutManager();
        notifyDataSetChanged();
        return this;
    }

    /**
     * 设置新的list数据并刷新adapter
     */
    public void setListAndNotifyDataSetChanged(List<BEAN> list) {
        if (list == null) {
            mList.clear();
        } else {
            mList = list;
        }
        notifyDataSetChanged();
    }

    @NonNull
    public List<BEAN> getList() {
        return mList;
    }

    /**
     * 删除指定adapter
     * 别忘了同步修改list数据和{@link #notifyDataSetChanged}
     *
     * @param adapterPosition 按添加顺序第几个
     */
    public BaseContainerAdapter<BEAN> removeAdapter(int adapterPosition) {
        mItemAdapters.remove(adapterPosition);
        return this;
    }

    /**
     * 删除指定adapter
     * 别忘了同步修改list数据和{@link #notifyDataSetChanged}
     *
     * @param adapterClass 哪个adapter
     */
    public BaseContainerAdapter<BEAN> removeAdapter(Class<? extends IContainerItemAdapter> adapterClass) {
        mItemAdapters.remove(adapterClass);
        return this;
    }

    /**
     * 清空adapter（数据不同步清空会崩溃哦）
     * 别忘了同步修改list数据和{@link #notifyDataSetChanged}
     */
    public BaseContainerAdapter<BEAN> removeAllAdapter() {
        mItemAdapters.clear();
        return this;
    }

    /**
     * 把rv的LayoutManager改成其他的GridLayoutManager时.此方法理论上没啥用
     */
    @SuppressWarnings("unchecked")//未检查警告,此处忽略
    public void changedLayoutManager(GridLayoutManager manager) {
        if (manager == null) return;
        mLayoutManager = manager;
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                mItemInfo.refreshItemInfo(position);
                IContainerItemAdapter itemAdapter = mItemInfo.mItemAdapter;
                itemAdapter.setCurrentBean(mList.get(mItemInfo.mListPosition));
                return itemAdapter.getSpanSize(mItemInfo.mItemPosition);
            }
        });
    }
}