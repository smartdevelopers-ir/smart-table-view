package ir.smartdevelopers.smarttable.views.TableView;

import android.util.SparseIntArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings({"unchecked", "rawtypes"})
final class SmartHorizontalAdapter extends RecyclerView.Adapter<SmartContentViewHolder> {
    private final SmartBaseTableAdapter mSmartBaseTableAdapter;
    private final int mRowPos;
    private final SmartTableView mSmartTableView;
    private SparseIntArray mWidthIntArray = new SparseIntArray();


    public SmartHorizontalAdapter(SmartBaseTableAdapter smartBaseTableAdapter,
                                  int rowPos, SmartTableView smartTableView) {
        mSmartBaseTableAdapter = smartBaseTableAdapter;
        this.mRowPos = rowPos;

        mSmartTableView = smartTableView;

    }

    @NonNull
    @Override
    public SmartContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mSmartBaseTableAdapter.onCreateContentViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull SmartContentViewHolder holder, int position) {

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

        int adapterPos = mRowPos * mSmartBaseTableAdapter.getColumnCount() + position;
        int colPos = adapterPos % mSmartBaseTableAdapter.getColumnCount();
        holder.setRowPosition(mRowPos);
        holder.setColumnPosition(colPos);
        holder.setContentPosition(adapterPos);
        mSmartBaseTableAdapter.onBindContentViewHolder(holder, adapterPos, mRowPos, colPos);
    }

    @Override
    public int getItemCount() {
        return mSmartBaseTableAdapter.getColumnCount();
    }


}
