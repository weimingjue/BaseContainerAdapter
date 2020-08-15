package com.wang.container.interfaces;

import androidx.annotation.NonNull;

import com.wang.container.bean.IContainerBean;
import com.wang.container.holder.BaseViewHolder;

/**
 * OnItemClickListener的接口
 * 见子类实现{@link OnItemClickListener}
 */
public interface IContainerItemClick<BEAN extends IContainerBean> extends IItemClick {

    void setCurrentViewHolder(@NonNull BaseViewHolder viewHolder);

    void setCurrentBean(@NonNull BEAN bean);

    /**
     * @return 当前的ViewHolder，每次想TA的时候get就对了，见实现类{@link OnItemClickListener#getCurrentBean}
     */
    @SuppressWarnings("unused")
    BaseViewHolder getCurrentViewHolder();

    /**
     * @return 当前的bean，每次想TA的时候get就对了，见实现类{@link OnItemClickListener#getCurrentBean}
     */
    @NonNull
    BEAN getCurrentBean();
}