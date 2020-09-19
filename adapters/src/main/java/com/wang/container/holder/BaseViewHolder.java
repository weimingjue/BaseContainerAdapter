package com.wang.container.holder;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 所有ViewHolder的基类
 */
public class BaseViewHolder<DB extends ViewDataBinding> extends RecyclerView.ViewHolder {
    public static final String TAG = "BaseViewHolder";
    private int mLvPosition = RecyclerView.NO_POSITION;

    @Nullable
    protected SparseArray<View> mViews;

    /**
     * DataBinding的创建移至{@link #getBinding()}
     */
    public BaseViewHolder(@NonNull View view) {
        super(view);
    }

    public BaseViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        this(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    /**
     * @return 如果是null则说明你的view不是基于dataBinding
     */
    public DB getBinding() {
        return getViewBinding(itemView);
    }

    /**
     * 如果你的{@link #itemView}不是真正的dataBinding，可以调用此方法
     */
    protected final DB getViewBinding(View bindingView) {
        //DataBindingUtil.bind的源码，这里判断只是为了减少异常打印
        DB binding = DataBindingUtil.getBinding(bindingView);
        if (binding != null) {
            return binding;
        }

        Object tagObj = bindingView.getTag();
        if (!(tagObj instanceof String)) {
            return null;
        }
        try {
            binding = DataBindingUtil.bind(bindingView);
        } catch (Exception e) {
            Log.e(TAG, "不是基于dataBinding的view: " + e.toString());
        }
        return binding;
    }

    @NonNull
    public Context getContext() {
        return itemView.getContext();
    }

    public <T extends View> T getView(@IdRes int resId) {
        if (mViews == null) {
            mViews = new SparseArray<>();
        }
        View view = mViews.get(resId);
        if (view == null) {
            view = itemView.findViewById(resId);
            mViews.put(resId, view);
        }
        //noinspection unchecked
        return (T) view;
    }

    /**
     * lv和rv都调用这个
     */
    public int getCommonPosition() {
        if (mLvPosition >= 0) {
            return mLvPosition;
        }
        int adapterPosition = getAdapterPosition();
        if (adapterPosition >= 0) {
            return adapterPosition;
        }
        return getLayoutPosition();
    }

    /**
     * listView需要手动设置position
     */
    public void setLvPosition(int position) {
        mLvPosition = position;
    }
}