package com.wang.container.bean

import com.wang.container.adapter.IContainerItemAdapter

class ItemAdapterPositionInfo {

    companion object {
        /**
         * 见下方相关方法[isFirst]
         */
        const val ABS_STATE_FIRST_LIST_POSITION = 0x1 //第一个
        const val ABS_STATE_LAST_LIST_POSITION = ABS_STATE_FIRST_LIST_POSITION shl 2 //最后一个
        const val ABS_STATE_HAS_HEADER = ABS_STATE_FIRST_LIST_POSITION shl 3 //有header
        const val ABS_STATE_HAS_FOOTER = ABS_STATE_FIRST_LIST_POSITION shl 4 //有footer
    }

    /**
     * list的position，container的position
     */
    val listPosition get() = _listPosition
    internal var _listPosition = 0

    /**
     * 子adapter对应的相对position
     */
    val itemPosition get() = _itemPosition
    internal var _itemPosition = 0

    /**
     * 当前position所在的位置信息，见下方相关方法[isFirst]等
     */
    val absState get() = _absState
    internal var _absState = 0

    val itemAdapter: IContainerItemAdapter<*>
        get() = _itemAdapter!!
    internal var _itemAdapter: IContainerItemAdapter<*>? = null

    /**
     * 是不是列表第一个（除了header）
     *
     *
     * 注意：整个adapter只有一个条目时既是第一个又是最后一个
     */
    val isFirst: Boolean
        get() = absState and ABS_STATE_FIRST_LIST_POSITION != 0

    /**
     * 是不是列表里中间的（不是header、也不是footer）
     */
    val isCenter: Boolean
        get() = !(isFirst || isLast)

    /**
     * 是不是列表最后一个（除了footer）
     *
     *
     * 注意：整个adapter只有一个条目时既是第一个又是最后一个
     */
    val isLast: Boolean
        get() = absState and ABS_STATE_LAST_LIST_POSITION != 0

    /**
     * 列表有没有header
     */
    val hasHeader: Boolean
        get() = absState and ABS_STATE_HAS_HEADER != 0

    /**
     * 列表有没有footer
     */
    val hasFooter: Boolean
        get() = absState and ABS_STATE_HAS_FOOTER != 0
}