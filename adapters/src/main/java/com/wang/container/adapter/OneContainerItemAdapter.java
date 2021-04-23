package com.wang.container.adapter;

import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.wang.container.BR;
import com.wang.container.bean.IContainerBean;
import com.wang.container.holder.BaseViewHolder;
import com.wang.container.utils.GenericUtils;

/**
 * 一个list的item仅对应一条数据，如：聊天
 */
public abstract class OneContainerItemAdapter<DB extends ViewDataBinding, BEAN extends IContainerBean> extends BaseContainerItemAdapter<BEAN> {
    /**
     * 如果构造不传，则一直是0
     */
    @LayoutRes
    protected final int mLayoutId;

    /**
     * 资源id已经不是必须的了
     * <p>
     * 无资源id有2种解决方式（任选其一）：
     * 1.什么都不做，根据泛型自动获取，但Proguard不能混淆{@link ViewDataBinding}的子类：
     * -keep class * extends androidx.databinding.ViewDataBinding
     * 2.覆盖{@link #onCreateChildViewHolder}，自己自定义即可
     */
    public OneContainerItemAdapter() {
        this(0);
    }

    public OneContainerItemAdapter(@LayoutRes int layoutId) {
        mLayoutId = layoutId;
    }

    @NonNull
    @Override
    protected final BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return onCreateChildViewHolder(parent);
    }

    @Override
    protected final void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        BEAN bean = getCurrentBean();
        if (holder.getBinding() != null) {
            holder.getBinding().setVariable(BR.bean, bean);
            holder.getBinding().setVariable(BR.adapter, this);
        }

        //noinspection unchecked
        onBindChildViewHolder(holder, bean);

        if (holder.getBinding() != null) {
            holder.getBinding().executePendingBindings();
        }
    }

    /**
     * 仅一条数据，不允许重写
     */
    @Override
    public final int getItemViewType(int position) {
        return 0;
    }

    @Override
    public final int getItemCount() {
        return 1;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 公共方法
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected BaseViewHolder<DB> onCreateChildViewHolder(ViewGroup parent) {
        if (mLayoutId == 0) {
            return new BaseViewHolder<>(GenericUtils.getGenericView(parent.getContext(), OneContainerItemAdapter.class, getClass(), parent));
        }
        return new BaseViewHolder<>(parent, mLayoutId);
    }

    /**
     * 当然还有{@link #getCurrentPositionInfo()}
     *
     * @param bean 就是{@link #getCurrentBean()}
     */
    protected abstract void onBindChildViewHolder(@NonNull BaseViewHolder<DB> holder, BEAN bean);
}
