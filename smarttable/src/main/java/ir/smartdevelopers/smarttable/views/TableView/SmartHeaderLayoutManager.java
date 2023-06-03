package ir.smartdevelopers.smarttable.views.TableView;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public final class SmartHeaderLayoutManager extends LinearLayoutManager {

    private RecyclerView mHeaderRecyclerView;
    private final SparseIntArray mCellsWidthHolder = new SparseIntArray();
    private SmartTableView mSmartTableView;

    public SmartHeaderLayoutManager(Context context) {
        super(context);
    }

    public SmartHeaderLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);

    }

    public SmartHeaderLayoutManager(Context context, int orientation, boolean reverseLayout,
                                    SmartTableView smartTableView) {
        super(context, orientation, reverseLayout);
        mSmartTableView = smartTableView;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mHeaderRecyclerView = view;
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

    public SparseIntArray getCellsWidthHolder() {
        return mCellsWidthHolder;
    }


}
