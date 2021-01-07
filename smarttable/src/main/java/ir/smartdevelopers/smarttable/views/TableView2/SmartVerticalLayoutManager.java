package ir.smartdevelopers.smarttable.views.TableView2;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SparseIntArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class SmartVerticalLayoutManager extends LinearLayoutManager {

    private SparseIntArray mCellsHeightHolder =new SparseIntArray();
    private  SmartTableView2 mSmartTableView2;
    private HorizontalScrollListener mHorizontalScrollListener;

    public SmartVerticalLayoutManager(Context context) {
        super(context);
    }

    public SmartVerticalLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);

    }

    public SmartVerticalLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SmartVerticalLayoutManager(Context context, int orientation, boolean reverseLayout, SmartTableView2 smartTableView2) {
        super(context, orientation, reverseLayout);
        mSmartTableView2=smartTableView2;
    }



    @Override
    public void measureChildWithMargins(@NonNull View child, int widthUsed, int heightUsed) {

        super.measureChildWithMargins(child, widthUsed, heightUsed);
        measureChild(child,widthUsed,heightUsed);
    }


    @Override
    public void measureChild(@NonNull View child, int widthUsed, int heightUsed) {

        mHorizontalScrollListener=mSmartTableView2.getHorizontalScrollListener();

        if (child instanceof SmartRecyclerView){
            SmartRecyclerView contentChild=(SmartRecyclerView) child;
            SmartHorizontalContentLayoutManager contentLayoutManager= (SmartHorizontalContentLayoutManager) contentChild.getLayoutManager();

            int scrollPosition=mHorizontalScrollListener.getScrollPosition();
            int scrollOffset=mHorizontalScrollListener.getScrollPositionOffset();
//            Log.v("TTT", "measureChild: scrollPosition="+scrollPosition);
//            Log.v("TTT", "measureChild: scrollOffset="+scrollOffset);

            if (scrollPosition>0){
                contentLayoutManager.scrollToPositionWithOffset(scrollPosition,scrollOffset);
            }
        }

//        super.measureChild(child, widthUsed, heightUsed);
        mCellsHeightHolder.put(getPosition(child),child.getMeasuredHeight());
//        Log.v("TTT","getMeasuredWidth "+getPosition(child)+" is :"+child.getMeasuredWidth());
    }

    public SparseIntArray getCellsWidthHolder() {
        return mCellsHeightHolder;
    }
    public int getMaxWidth(){
        return SparseUtil.getMax(mCellsHeightHolder);
    }
}
