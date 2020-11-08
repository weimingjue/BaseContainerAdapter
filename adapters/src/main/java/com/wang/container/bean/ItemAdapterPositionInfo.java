package com.wang.container.bean;

import com.wang.container.adapter.IContainerItemAdapter;

public class ItemAdapterPositionInfo {

    /**
     * 见下方相关方法{@link #isFirst}
     */
    public static final int ABS_STATE_FIRST_LIST_POSITION = 0x1;//第一个
    public static final int ABS_STATE_LAST_LIST_POSITION = ABS_STATE_FIRST_LIST_POSITION << 2;//最后一个
    public static final int ABS_STATE_HAS_HEADER = ABS_STATE_FIRST_LIST_POSITION << 3;//有header
    public static final int ABS_STATE_HAS_FOOTER = ABS_STATE_FIRST_LIST_POSITION << 4;//有footer


    /**
     * list的position，大list的position
     */
    public int mListPosition;
    /**
     * 子adapter对应的相对position
     */
    public int mItemPosition;
    /**
     * 当前position所在的位置信息，见下方相关方法{@link #isFirst}
     */
    public int mAbsState;

    public IContainerItemAdapter mItemAdapter;

    /**
     * 是不是列表第一个（除了header）
     * <p>
     * 注意：整个adapter只有一个条目时既是第一个又是最后一个
     */
    public boolean isFirst() {
        return (mAbsState & ABS_STATE_FIRST_LIST_POSITION) != 0;
    }

    /**
     * 是不是列表里中间的（不是header、也不是footer）
     */
    public boolean isCenter() {
        return !(isFirst() || isLast());
    }

    /**
     * 是不是列表最后一个（除了footer）
     * <p>
     * 注意：整个adapter只有一个条目时既是第一个又是最后一个
     */
    public boolean isLast() {
        return (mAbsState & ABS_STATE_LAST_LIST_POSITION) != 0;
    }

    /**
     * 列表有没有header
     */
    public boolean hasHeader() {
        return (mAbsState & ABS_STATE_HAS_HEADER) != 0;
    }

    /**
     * 列表有没有footer
     */
    public boolean hasFooter() {
        return (mAbsState & ABS_STATE_HAS_FOOTER) != 0;
    }
}
