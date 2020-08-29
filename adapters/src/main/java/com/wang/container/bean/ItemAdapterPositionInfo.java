package com.wang.container.bean;

import com.wang.container.adapter.IContainerItemAdapter;

public class ItemAdapterPositionInfo {

    /**
     * 当前view在整个recyclerview的位置状态
     * 例：是不是最后一个(mAbsState & ABS_STATE_LAST_LIST_POSITION )!= 0
     * 有没有header (mAbsState & ABS_STATE_HAS_HEADER )!= 0
     * 注意：整个adapter只有一个条目时既是第一个又是最后一个
     */
    public static final int ABS_STATE_FIRST_LIST_POSITION = 0x1;//第一个
    public static final int ABS_STATE_CENTER_POSITION = ABS_STATE_FIRST_LIST_POSITION << 1;//中间
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
     * 当前position所在的位置信息，见上方静态常量
     */
    public int mAbsState;

    public IContainerItemAdapter mItemAdapter;
}
