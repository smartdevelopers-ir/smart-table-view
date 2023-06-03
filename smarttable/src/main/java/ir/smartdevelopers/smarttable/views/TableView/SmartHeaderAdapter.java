package ir.smartdevelopers.smarttable.views.TableView;

import android.util.SparseIntArray;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("rawtypes")
final class SmartHeaderAdapter extends RecyclerView.Adapter<SmartHeaderViewHolder> {
    private final SmartBaseTableAdapter mSmartBaseTableAdapter;
    private final SmartTableView mSmartTableView;
    private SparseIntArray mWidthIntArray = new SparseIntArray();
    private SmartBaseTableAdapter.HeaderObserver mHeaderObserver;

    public SmartHeaderAdapter(final SmartBaseTableAdapter smartBaseTableAdapter, SmartTableView smartTableView) {
        mSmartBaseTableAdapter = smartBaseTableAdapter;
        mSmartTableView = smartTableView;
        mHeaderObserver = new SmartBaseTableAdapter.HeaderObserver() {
            @Override
            public void onHeaderItemChanged(int position, boolean changeSize) {
                notifyItemChanged(position);
                if (changeSize) {
                    for (int i = 0; i < mSmartBaseTableAdapter.getRowCount(); i++) {
                        smartBaseTableAdapter.notifyContentItemChanged(i, position, true);
                    }
                }
            }
        };
        mSmartBaseTableAdapter.setHeaderObserver(mHeaderObserver);
    }


    @NonNull
    @Override
    public SmartHeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mSmartBaseTableAdapter.onCreateHeaderViewHolder(parent, viewType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull SmartHeaderViewHolder holder, int position) {
        if (mSmartTableView.isStretchIfNotFit() && !mSmartTableView.isFitHorizontally()) {
            if (mSmartTableView.getWidthIntArray().get(position) == 0) {
                mWidthIntArray = mSmartTableView.calculateStretchWidth();
            }

        } else if (mSmartTableView.isFitHorizontally()) {
            if (mSmartTableView.getWidthIntArray().get(position) == 0) {
                /*if we have calculated before just set another value o this position*/
                if (position - 1 >= 0) {
                    if (mSmartTableView.getWidthIntArray().get(position - 1) != 0) {
                        mSmartTableView.getWidthIntArray().put(position,
                                mSmartTableView.getWidthIntArray().get(position - 1));
                    } else {
                        mWidthIntArray = mSmartTableView.calculateAverageWidth();
                    }
                }
            }

        }
        mWidthIntArray = mSmartTableView.getWidthIntArray();

        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        int max = mWidthIntArray.get(position);
        if (max == 0) {
            max = mSmartBaseTableAdapter.getMaxColumnWidth(position);
            mWidthIntArray.put(position, max);
        }
        params.width = max;

        mSmartBaseTableAdapter.onBindHeaderViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mSmartBaseTableAdapter.getColumnCount();
    }
}
