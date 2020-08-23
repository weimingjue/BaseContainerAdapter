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

import com.wang.container.R;

/**
 * 所有ViewHolder的基类
 */
public class BaseViewHolder<DB extends ViewDataBinding> extends RecyclerView.ViewHolder {
    public static final String TAG = "BaseViewHolder";
    private int mLvPosition = RecyclerView.NO_POSITION;

    @Nullable
    protected SparseArray<View> mViews;

    /**
     * null不null自己知道
     */
    private final DB mBinding;

    public BaseViewHolder(@NonNull View view) {
        super(view);
        DB binding = null;
        //如果非null，则说明不支持dataBinding
        if (view.getTag(R.id.tag_view_no_data_binding) == null) {
            try {
                binding = DataBindingUtil.bind(view);
            } catch (Exception e) {
                Log.e(TAG, "不是基于dataBinding的view: " + e.toString());
            }
        }
        mBinding = binding;
    }

    public BaseViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        this(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    public DB getBinding() {
        return mBinding;
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