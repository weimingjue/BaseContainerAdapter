package com.wang.adapters.listener;

import android.view.View;

import com.wang.adapters.bean.IContainerBean;

/**
 * OnItemClickListener的接口
 * 见子类实现{@link OnItemClickListener}
 */
public interface IItemClick<BEAN extends IContainerBean> extends View.OnClickListener, View.OnLongClickListener {
    /**
     * @return 当前的bean，每次想TA的时候get就对了，见实现类{@link OnItemClickListener#getCurrentBean}
     */
    BEAN getCurrentBean();
}