package ir.smartdevelopers.smarttable.views.TableView2;

import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public final class SmartHorizontalContentLayoutManager extends LinearLayoutManager {

    private SparseIntArray mCellsWidthHolder =new SparseIntArray();
//    private  SmartHorizontalLayoutManager mHeaderLayoutManager;
    private  SmartTableView2 mSmartTableView2;
    public SmartHorizontalContentLayoutManager(Context context) {
        super(context);
    }

    public SmartHorizontalContentLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);

    }

    public SmartHorizontalContentLayoutManager(Context context, int orientation, boolean reverseLayout,
                                               SmartTableView2 smartTableView2) {
        super(context, orientation, reverseLayout);
        mSmartTableView2=smartTableView2;
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
//            measureChild(child,widthUsed,heightUsed);
        mCellsWidthHolder.put(getPosition(child),child.getMeasuredHeight());



    }


    @Override
    public void measureChild(@NonNull View child, int widthUsed, int heightUsed) {

        super.measureChild(child, widthUsed, heightUsed);

        mCellsWidthHolder.put(getPosition(child),child.getMeasuredHeight());

//        Log.v("TTT","getMeasuredWidth "+getClass().getName()+" "+getPosition(child)+" is :"+child.getMeasuredWidth());
    }



    public SparseIntArray getCellsWidthHolder() {
        return mCellsWidthHolder;
    }

    public int getMaxWidth(){
        return SparseUtil.getMax(mCellsWidthHolder);
    }

    @SuppressWarnings("ConstantConditions")
    private int getMaxColumnWidth(int position){

        SparseIntArray intArray=new SparseIntArray();
        int childrenCount=mSmartTableView2.getContentRecyclerView().getChildCount();
        for (int i=0;i<childrenCount;i++){
            SmartHorizontalContentLayoutManager horizontalLayoutManager=
                    (SmartHorizontalContentLayoutManager) ((SmartRecyclerView)mSmartTableView2.getContentRecyclerView().getChildAt(i)).getLayoutManager();
            intArray.put(i,horizontalLayoutManager.getCellsWidthHolder().get(position));
        }
        intArray.put(intArray.size(),mSmartTableView2.getHeaderLayoutManager().getCellsWidthHolder().get(position));
        return SparseUtil.getMax(intArray);
    }


}
