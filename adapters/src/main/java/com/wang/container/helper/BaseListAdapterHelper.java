package com.wang.container.helper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.container.holder.BaseViewHolder;
import com.wang.container.interfaces.IListAdapter;

import java.util.ArrayList;
import java.util.List;

public class BaseListAdapterHelper<BEAN> {

    @NonNull
    public final List<BEAN> mList;

    public View mHeaderView, mFooterView;

    public final IListAdapter mAdapter;

    public BaseListAdapterHelper(IListAdapter adapter, @Nullable List<BEAN> list) {
        mAdapter = adapter;
        mList = list == null ? new ArrayList<>() : list;
    }

    @NonNull
    public BaseViewHolder onCreateHeaderFooterViewHolder(@NonNull ViewGroup parent) {
        FrameLayout fl = new FrameLayout(parent.getContext());
        fl.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new BaseViewHolder(fl);
    }

    public void onBindHeaderFooterViewHolder(@NonNull BaseViewHolder holder, View headerOrFooterView) {
        FrameLayout fl = (FrameLayout) holder.itemView;
        @Nullable
        ViewGroup oldParent = (ViewGroup) headerOrFooterView.getParent();
        if (oldParent != fl) {
            if (oldParent != null) {
                oldParent.removeView(headerOrFooterView);
            }
            if (fl.getChildCount() > 0) {
                fl.removeAllViews();
            }
            fl.addView(headerOrFooterView);
        }
    }

    /**
     * ContainerAdapter没点击事件回调，需要自己写
     * BaseAdapterRvList、BaseAdapterLvsList的点击事件在OnItemClickListener中有
     *
     * @param view null表示删除，view的parent为FrameLayout，默认match、wrap
     */
    public void setHeaderView(@Nullable View view) {
        View oldHeaderView = mHeaderView;//旧view
        mHeaderView = view;
        if (mHeaderView != null && mHeaderView.getLayoutParams() == null) {
            mHeaderView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        //4种情况
        if (view == null && oldHeaderView != null) {
            if (mAdapter instanceof RecyclerView.Adapter) {
                ((RecyclerView.Adapter) mAdapter).notifyItemRemoved(0);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else if (view != null && oldHeaderView == null) {
            if (mAdapter instanceof RecyclerView.Adapter) {
                ((RecyclerView.Adapter) mAdapter).notifyItemInserted(0);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else if (view != oldHeaderView) {
            if (mAdapter instanceof RecyclerView.Adapter) {
                ((RecyclerView.Adapter) mAdapter).notifyItemChanged(0);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }//else相等忽略
    }

    /**
     * ContainerAdapter没点击回调，需要自己写
     * BaseAdapterRvList、BaseAdapterLvsList的点击事件在OnItemClickListener中有
     *
     * @param view null表示删除，view的parent为FrameLayout，默认match、wrap
     */
    public void setFooterView(@Nullable View view) {
        View oldFooterView = mFooterView;//旧view
        mFooterView = view;
        if (mFooterView != null && mFooterView.getLayoutParams() == null) {
            mFooterView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        //4种情况
        if (view == null && oldFooterView != null) {
            if (mAdapter instanceof RecyclerView.Adapter) {
                ((RecyclerView.Adapter) mAdapter).notifyItemRemoved(mAdapter.getItemCount());//count已经减一了，所以不用减了
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else if (view != null && oldFooterView == null) {
            if (mAdapter instanceof RecyclerView.Adapter) {
                ((RecyclerView.Adapter) mAdapter).notifyItemInserted(mAdapter.getItemCount() - 1);//count已经加一了，所以需要减掉
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else if (view != oldFooterView) {
            if (mAdapter instanceof RecyclerView.Adapter) {
                ((RecyclerView.Adapter) mAdapter).notifyItemChanged(mAdapter.getItemCount() - 1);//count不变
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }//else相等忽略
    }
}
