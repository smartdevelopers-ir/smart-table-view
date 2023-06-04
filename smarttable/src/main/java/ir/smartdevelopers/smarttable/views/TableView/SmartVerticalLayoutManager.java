package ir.smartdevelopers.smarttable.views.TableView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class SmartVerticalLayoutManager extends LinearLayoutManager {

    private final SparseIntArray mCellsHeightHolder = new SparseIntArray();
    private SmartTableView mSmartTableView;

    public SmartVerticalLayoutManager(Context context) {
        super(context);
    }

    public SmartVerticalLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);

    }

    public SmartVerticalLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SmartVerticalLayoutManager(Context context, int orientation, boolean reverseLayout, SmartTableView smartTableView) {
        super(context, orientation, reverseLayout);
        mSmartTableView = smartTableView;
    }


    @Override
    public void measureChildWithMargins(@NonNull View child, int widthUsed, int heightUsed) {
        measureChild(child, widthUsed, heightUsed);
        int pos = getPosition(child);
        int height = mSmartTableView.getHeightSizeArray().get(pos);
        if (height != 0) {
            if (child.getLayoutParams() != null) {
                child.getLayoutParams().height = height;
            }
            super.measureChildWithMargins(child, widthUsed, height);
        } else {
            super.measureChildWithMargins(child, widthUsed, heightUsed);
        }
    }


    @Override
    public void measureChild(@NonNull View child, int widthUsed, int heightUsed) {
//        super.measureChild(child,widthUsed,heightUsed);
        HorizontalScrollListener horizontalScrollListener = mSmartTableView.getHorizontalScrollListener();

        if (child instanceof SmartRecyclerView) {
            SmartRecyclerView contentChild = (SmartRecyclerView) child;
            SmartHorizontalContentLayoutManager contentLayoutManager = (SmartHorizontalContentLayoutManager) contentChild.getLayoutManager();

            int scrollPosition = horizontalScrollListener.getScrollPosition();
            int scrollOffset = horizontalScrollListener.getScrollPositionOffset();
            Log.d("TTT",String.format("scrollPosition = %d , scrollOffset = %d",scrollPosition,scrollOffset));
            if (scrollPosition > 0) {
                contentLayoutManager.scrollToPositionWithOffset(scrollPosition, scrollOffset);
            }
        }
        mCellsHeightHolder.put(getPosition(child), child.getMeasuredHeight());
    }



    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
    }

    public SparseIntArray getCellsHeightHolder() {
        return mCellsHeightHolder;
    }


    public int getMaxWidth() {
        return SparseUtil.getMax(mCellsHeightHolder);
    }
}
