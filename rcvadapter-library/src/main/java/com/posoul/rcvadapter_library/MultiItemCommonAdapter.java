package com.posoul.rcvadapter_library;

import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zhangld on 2016/9/18.
 */
public abstract class MultiItemCommonAdapter<T> extends CommonAdapter<T> {
    protected MultiItemTypeSupport<T> mMultiItemTypeSupport;

    public MultiItemCommonAdapter(Context context, List<T> data,
                                  MultiItemTypeSupport<T> multiItemTypeSupport) {
        super(context, -1, data);
        mMultiItemTypeSupport = multiItemTypeSupport;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return mFootViews.keyAt(position - getHeadersCount() - mData.size());
        }
        if (isShowLoadMore(position)) {
            return ITEM_TYPE_LOAD_MORE;
        }
        return mMultiItemTypeSupport.getItemViewType(position - getHeadersCount(), mData.get(position - getHeadersCount()));
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            BaseViewHolder baseViewHolder = BaseViewHolder.createViewHolder(parent.getContext(), mHeaderViews.get(viewType));
            //setListener(parent, baseViewHolder, viewType);
            return baseViewHolder;

        } else if (mFootViews.get(viewType) != null) {
            BaseViewHolder baseViewHolder = BaseViewHolder.createViewHolder(parent.getContext(), mFootViews.get(viewType));
            //setListener(parent, baseViewHolder, viewType);
            return baseViewHolder;
        } else if (viewType == ITEM_TYPE_LOAD_MORE) {
            BaseViewHolder baseViewHolder;
            if (!NoMore) {
                if (mLoadMoreView != null) {
                    baseViewHolder = BaseViewHolder.createViewHolder(parent.getContext(), mLoadMoreView);
                } else {
                    baseViewHolder = BaseViewHolder.createViewHolder(parent.getContext(), parent, mLoadMoreLayoutId);
                }
            } else {
                if (mNoMoreView != null) {
                    baseViewHolder = BaseViewHolder.createViewHolder(parent.getContext(), mNoMoreView);
                } else if (mNoMoreLayoutId != 0) {
                    baseViewHolder = BaseViewHolder.createViewHolder(parent.getContext(), parent, mNoMoreLayoutId);
                } else {
                    baseViewHolder = null;
                }
            }
            return baseViewHolder;
        }
        int layoutId = mMultiItemTypeSupport.getLayoutId(viewType);
        BaseViewHolder baseViewHolder = BaseViewHolder.createViewHolder(mContext, parent, layoutId);
        setListener(parent, baseViewHolder, viewType);
        return baseViewHolder;


    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (isHeaderViewPos(position)) {
            return;
        }
        if (isFooterViewPos(position)) {
            return;
        }
        if (isShowLoadMore(position)) {
            if (mOnLoadMoreListener != null && !NoMore) {
                mOnLoadMoreListener.onLoadMoreRequested();
            }
            return;
        }
        convert(holder, mData.get(position - getHeadersCount()), getItemViewType(position));
    }

}
