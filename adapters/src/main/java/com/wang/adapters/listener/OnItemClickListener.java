package com.wang.adapters.listener;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.wang.adapters.R;
import com.wang.adapters.bean.IContainerBean;

/**
 * 点击,长按,header,footer的回调
 * 完美解决类似recyclerviewAdapter的setOnClickListener重复new对象的问题
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class OnItemClickListener<BEAN extends IContainerBean> implements IItemClick<BEAN> {

    private BEAN mCurrentBean;

    @Override
    public final void onClick(View view) {
        mCurrentBean = getViewBean(view);
        onItemClick(view, getViewPosition(view));
    }

    @Override
    public final boolean onLongClick(View view) {
        mCurrentBean = getViewBean(view);
        return onItemLongClick(view, getViewPosition(view));
    }

    /**
     * 获取当前view所保存的position
     */
    protected final int getViewPosition(View view) {
        return (int) view.getTag(R.id.tag_view_click);
    }

    /**
     * 获取当前view所保存的bean
     */
    @SuppressWarnings("unchecked")
    protected final BEAN getViewBean(View view) {
        return (BEAN) view.getTag(R.id.tag_view_bean);
    }

    /**
     * 获取当前view所在的ViewHolder
     */
    protected final RecyclerView.ViewHolder getViewHolder(View view) {
        return (RecyclerView.ViewHolder) view.getTag(R.id.tag_view_holder);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 以下是item的点击、长按和bean
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @return 当前的bean，每次想TA的时候get就对了
     * 用到的地方{@link #onItemClick}{@link #onItemLongClick}
     */
    @Override
    public final BEAN getCurrentBean() {
        return mCurrentBean;
    }

    /**
     * item被点击时
     *
     * @param position 属于该adapter的position
     */
    protected abstract void onItemClick(View view, int position);

    /**
     * item被长按时
     */
    protected boolean onItemLongClick(View view, int position) {
        return false;
    }
}