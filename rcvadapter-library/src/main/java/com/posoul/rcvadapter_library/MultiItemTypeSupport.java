package com.posoul.rcvadapter_library;

/**
 * Created by zhangld on 2016/9/18.
 */
public interface MultiItemTypeSupport<T> {
    int getLayoutId(int itemType);

    int getItemViewType(int position, T t);
}
