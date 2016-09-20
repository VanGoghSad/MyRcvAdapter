package com.posoul.rcvadapter_library;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Liw on 2016/9/18.
 */
public abstract class CommonAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;
    protected static final int ITEM_TYPE_LOAD_MORE = Integer.MAX_VALUE - 2;

    /**
     * header and footer
     */
    protected SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    protected SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();

    /**
     * loadMore item
     */
    protected boolean NoMore = false;
    protected View mLoadMoreView;
    protected View mNoMoreView;
    protected int mLoadMoreLayoutId;
    protected int mNoMoreLayoutId;
    protected OnLoadMoreListener mOnLoadMoreListener;

    /**
     * common item
     */
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mData;
    protected LayoutInflater mInflater;
    protected OnItemClickListener mOnItemClickListener;


    public CommonAdapter(Context context, int layoutId, List<T> data) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mData = data;
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

        BaseViewHolder baseViewHolder = BaseViewHolder.createViewHolder(mContext, parent, mLayoutId);
        setListener(parent, baseViewHolder, viewType);
        return baseViewHolder;
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
        return super.getItemViewType(position - getHeadersCount());
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

    @Override
    public int getItemCount() {
        if (!NoMore) {
            return mData.size() + getHeadersCount() + getFootersCount() + (hasLoadMore() ? 1 : 0);
        } else {
            return mData.size() + getHeadersCount() + getFootersCount() + (hasNoMore() ? 1 : 0);
        }
    }

    protected abstract void convert(BaseViewHolder holder, T t, int itemType);

    protected boolean isEnabled(int viewType) {
        return true;
    }

    protected void setListener(final ViewGroup parent, final BaseViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder, position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public void removeItem(int position) {
        mData.remove(position - getHeadersCount());
        notifyItemRemoved(position);
    }

    /**
     * header and footer
     * ---------------------------------------------------------------------------------------------
     */
    protected boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    protected boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + mData.size() && position < getHeadersCount() + mData.size() + getFootersCount();
    }


    public void addHeaderView(View view) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addFootView(View view) {
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFootViews.size();
    }

    /**
     * about loadMore
     * ---------------------------------------------------------------------------------------------
     */
    protected boolean hasLoadMore() {
        return mLoadMoreView != null || mLoadMoreLayoutId != 0;
    }

    protected boolean hasNoMore() {
        return mNoMoreView != null || mNoMoreLayoutId != 0;
    }


    protected boolean isShowLoadMore(int position) {
        return hasLoadMore() && (position >= (mData.size() + getHeadersCount() + getFootersCount()));
    }


    public interface OnLoadMoreListener {
        void onLoadMoreRequested();
    }



    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        if (loadMoreListener != null) {
            mOnLoadMoreListener = loadMoreListener;
        }
    }

    public void setLoadMoreView(View loadMoreView) {
        mLoadMoreView = loadMoreView;
    }

    public void setLoadMoreView(int loadMoreLayoutId, int noMoreLayoutId) {
        mLoadMoreLayoutId = loadMoreLayoutId;
        mNoMoreLayoutId = noMoreLayoutId;
    }

    public void setIsNoMore(boolean isNoMore) {
        NoMore = isNoMore;
    }



}
