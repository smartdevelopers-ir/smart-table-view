package ir.smartdevelopers.smarttable.views.TableView;

import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("rawtypes")
final class SmartContentAdapter extends RecyclerView.Adapter<SmartContentAdapter.ContentRowViewHolder> implements
        OnBindListener {

    private final SmartBaseTableAdapter mSmartBaseTableAdapter;
    private final RecyclerView.RecycledViewPool mRecycledViewPool = new RecyclerView.RecycledViewPool();
    private RecyclerView.OnItemTouchListener mOnItemTouchListener;
    private final SmartVerticalLayoutManager mSidebarLayoutManager;
    private final SmartTableView mSmartTableView;


    public SmartContentAdapter(final SmartBaseTableAdapter smartBaseTableAdapter, final SmartTableView smartTableView) {
        mSmartBaseTableAdapter = smartBaseTableAdapter;
        mSidebarLayoutManager = smartTableView.getSidebarLayoutManager();
        mSmartTableView = smartTableView;
        SmartBaseTableAdapter.ContentObserver contentObserver = new SmartBaseTableAdapter.ContentObserver() {
            @Override
            public void onContentItemChanged(int rowPos, int colPos, boolean changeSize) {
                SmartVerticalLayoutManager manager = (SmartVerticalLayoutManager) mSmartTableView.getContentRecyclerView().getLayoutManager();
                if (manager == null) {
                    return;
                }
                if (changeSize) {

                    mSmartTableView.getHeightSizeArray().delete(rowPos);
                    mSmartTableView.getWidthSizeArray().delete(colPos);
                    if (mSmartTableView.headerIsShowing()) {
                        Parcelable recyclerViewState = smartTableView.getHeaderLayoutManager().onSaveInstanceState();
                        //noinspection ConstantConditions
                        mSmartTableView.getHeaderRecyclerView().getAdapter().notifyItemChanged(colPos);

                        smartTableView.getHeaderLayoutManager().onRestoreInstanceState(recyclerViewState);
                    }
                    for (int i = 0; i < smartBaseTableAdapter.getRowCount(); i++) {
                        if (i == rowPos) {
                            continue;
                        }
                        smartBaseTableAdapter.notifyContentItemChanged(i, colPos, false);
                    }
                }
                View rowView = manager.findViewByPosition(rowPos);
                if (rowView instanceof SmartRecyclerView) {
                    RecyclerView.Adapter adapter = ((SmartRecyclerView) rowView).getAdapter();
                    if (adapter != null) {
                        adapter.notifyItemChanged(colPos);
                    }
                }

            }
        };
        smartBaseTableAdapter.setContentObserver(contentObserver);
    }

    @NonNull
    @Override
    public ContentRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SmartRecyclerView innerRV = new SmartRecyclerView(parent.getContext());
        innerRV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        innerRV.setRecycledViewPool(mRecycledViewPool);
        innerRV.setItemAnimator(null);
        innerRV.setLayoutDirection(parent.getLayoutDirection());
        if (mSmartTableView.getContentColumnItemDecoration() != null) {
            innerRV.addItemDecoration(mSmartTableView.getContentColumnItemDecoration());
        }
        innerRV.setLayoutManager(new SmartHorizontalContentLayoutManager(parent.getContext(), RecyclerView.HORIZONTAL, false,
                mSmartTableView));
        if (mOnItemTouchListener != null) {
            innerRV.addOnItemTouchListener(mOnItemTouchListener);
        }
        return new ContentRowViewHolder(innerRV);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentRowViewHolder holder, int position) {

        SmartRecyclerView rowRecyclerView = (SmartRecyclerView) holder.itemView;
        SmartHorizontalAdapter horizontalAdapter = new SmartHorizontalAdapter(mSmartTableView.getAdapter(), position,
                mSmartTableView);
        if (mSmartTableView.getHeightSizeArray().get(position) != 0) {
            rowRecyclerView.setHasFixedSize(true);
        } else {
            rowRecyclerView.setHasFixedSize(false);
        }

        rowRecyclerView.setAdapter(horizontalAdapter);
        if (mSmartTableView.sideBarIsShowing()) {
            int contentRowHeight = SmartTableUtil.measureHeight(rowRecyclerView);
            int sidebarHeight = mSidebarLayoutManager.getCellsHeightHolder().get(position);
            if (sidebarHeight == 0) {
                View sidebar = mSidebarLayoutManager.findViewByPosition(position);
                if (sidebar != null) {
                    sidebarHeight = SmartTableUtil.measureHeight(sidebar);
                } else {
                    mSmartTableView.getSidebarNullLayoutManagerHolder().put(position, true);
                }
            }

            int maxHeight = Math.max(contentRowHeight, sidebarHeight);
            if (sidebarHeight != 0 && mSmartTableView.getHeightSizeArray().get(position) == 0)
                mSmartTableView.getHeightSizeArray().put(position, maxHeight);
            if (maxHeight > sidebarHeight) {
                // notify sidebar item changed
                SmartSidebarAdapter sidebarAdapter = (SmartSidebarAdapter) mSmartTableView.getSidebarRecyclerView().getAdapter();
                if (sidebarAdapter != null) {
                    sidebarAdapter.notifyItemChanged(position);
                }
            }

        }

    }


    @Override
    public int getItemCount() {
        return mSmartBaseTableAdapter.getRowCount();
    }

    public void setOnItemTouchListener(RecyclerView.OnItemTouchListener onItemTouchListener) {
        mOnItemTouchListener = onItemTouchListener;
    }

    @Override
    public void onBind(int position) {
        if (mSidebarLayoutManager != null && mSidebarLayoutManager.getCellsHeightHolder().get(position) == 0)
            notifyItemChanged(position);
    }


    @Override
    public void onViewAttachedToWindow(@NonNull ContentRowViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        SmartRecyclerView rowRecyclerView = (SmartRecyclerView) holder.itemView;
        int rowScrollX = rowRecyclerView.computeHorizontalScrollOffset();
        int lastScrollX = mSmartTableView.getHorizontalScrollListener().getXPosition();
        if (rowScrollX != lastScrollX){
                int diff = lastScrollX - rowScrollX;
                rowRecyclerView.scrollBy(diff,0);
            }
    }

    static class ContentRowViewHolder extends RecyclerView.ViewHolder {

        public ContentRowViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

}
