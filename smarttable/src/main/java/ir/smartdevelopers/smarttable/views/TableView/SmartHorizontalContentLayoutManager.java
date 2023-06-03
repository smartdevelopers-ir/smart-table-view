package ir.smartdevelopers.smarttable.views.TableView;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public final class SmartHorizontalContentLayoutManager extends LinearLayoutManager {

    private final SparseIntArray mCellsWidthHolder = new SparseIntArray();
    //    private  SmartHorizontalLayoutManager mHeaderLayoutManager;
    private SmartTableView mSmartTableView;

    public SmartHorizontalContentLayoutManager(Context context) {
        super(context);
    }

    public SmartHorizontalContentLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);

    }

    public SmartHorizontalContentLayoutManager(Context context, int orientation, boolean reverseLayout,
                                               SmartTableView smartTableView) {
        super(context, orientation, reverseLayout);
        mSmartTableView = smartTableView;
        setItemPrefetchEnabled(true);
        setInitialPrefetchItemCount(10);
    }


    @Override
    public void scrollToPositionWithOffset(int position, int offset) {
        super.scrollToPositionWithOffset(position, offset);
    }

    @Override
    public void measureChildWithMargins(@NonNull View child, int widthUsed, int heightUsed) {
        super.measureChildWithMargins(child, widthUsed, heightUsed);
        mCellsWidthHolder.put(getPosition(child), child.getMeasuredWidth());

    }


    @Override
    public void measureChild(@NonNull View child, int widthUsed, int heightUsed) {
        super.measureChild(child, widthUsed, heightUsed);
        mCellsWidthHolder.put(getPosition(child), child.getMeasuredWidth());
    }


    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        if (view instanceof SmartRecyclerView) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) mSmartTableView.getContentRecyclerView().getLayoutManager();
            if (layoutManager != null) {
                int firsPos = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (firsPos != -1) {
                    View firstView = layoutManager.findViewByPosition(firsPos);
                    if (firstView instanceof SmartRecyclerView) {
                        SmartRecyclerView firstRecyleView = (SmartRecyclerView) firstView;
                        SmartRecyclerView newView = (SmartRecyclerView) view;
                        if (newView.getScrolledX() != firstRecyleView.getScrolledY()) {
                            int diff = Math.abs(newView.getScrolledX() - firstRecyleView.getScrolledY());
                            newView.scrollBy(diff, 0);
                        }
                    }
                }
            }


        }
    }

    public SparseIntArray getCellsWidthHolder() {
        return mCellsWidthHolder;
    }

    public int getMaxWidth() {
        return SparseUtil.getMax(mCellsWidthHolder);
    }

    @SuppressWarnings("ConstantConditions")
    private int getMaxColumnWidth(int position) {

        SparseIntArray intArray = new SparseIntArray();
        int childrenCount = mSmartTableView.getContentRecyclerView().getChildCount();
        for (int i = 0; i < childrenCount; i++) {
            SmartHorizontalContentLayoutManager horizontalLayoutManager =
                    (SmartHorizontalContentLayoutManager) ((SmartRecyclerView) mSmartTableView.getContentRecyclerView().getChildAt(i)).getLayoutManager();
            intArray.put(i, horizontalLayoutManager.getCellsWidthHolder().get(position));
        }
        intArray.put(intArray.size(), mSmartTableView.getHeaderLayoutManager().getCellsWidthHolder().get(position));
        return SparseUtil.getMax(intArray);
    }


}
