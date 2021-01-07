package ir.smartdevelopers.smarttable.views.TableView2;

import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public final class SmartHeaderLayoutManager extends LinearLayoutManager {

    private RecyclerView mHeaderRecyclerView;
    private SparseIntArray mCellsWidthHolder =new SparseIntArray();
//    private  SmartHorizontalLayoutManager mHeaderLayoutManager;
    private  SmartTableView2 mSmartTableView2;
    public SmartHeaderLayoutManager(Context context) {
        super(context);
    }

    public SmartHeaderLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);

    }

    public SmartHeaderLayoutManager(Context context, int orientation, boolean reverseLayout,
                                    SmartTableView2 smartTableView2) {
        super(context, orientation, reverseLayout);
        mSmartTableView2=smartTableView2;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mHeaderRecyclerView=view;
    }

    @Override
    public void measureChildWithMargins(@NonNull View child, int widthUsed, int heightUsed) {

            super.measureChildWithMargins(child, widthUsed, heightUsed);
//            measureChild(child,widthUsed,heightUsed);
        mCellsWidthHolder.put(getPosition(child),child.getMeasuredWidth());



    }


    @Override
    public void measureChild(@NonNull View child, int widthUsed, int heightUsed) {

            super.measureChild(child, widthUsed, heightUsed);

        mCellsWidthHolder.put(getPosition(child),child.getMeasuredWidth());

    }

    public SparseIntArray getCellsWidthHolder() {
        return mCellsWidthHolder;
    }




}
