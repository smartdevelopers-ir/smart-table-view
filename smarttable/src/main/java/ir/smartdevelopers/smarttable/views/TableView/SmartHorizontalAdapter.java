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

        if (mSmartTableView.isStretchIfNotFitEnabled() && !mSmartTableView.isFitHorizontally()) {
            if (mSmartTableView.getWidthSizeArray().get(position) == 0) {
                 mSmartTableView.calculateStretchWidth();
            }

        } else if (mSmartTableView.isFitHorizontally()) {
            if (mSmartTableView.getWidthSizeArray().get(position) == 0) {
                mSmartTableView.calculateAverageWidth();
            }

        }
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        int max = mSmartTableView.getWidthSizeArray().get(position);
        if (max == 0) {
            max = mSmartTableView.getMaxColumnWidth(position);
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
