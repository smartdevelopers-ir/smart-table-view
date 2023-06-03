package ir.smartdevelopers.smarttable.views.TableView;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("rawtypes")
final class SmartSidebarAdapter extends RecyclerView.Adapter<SmartSidebarViewHolder> {
    private final SmartBaseTableAdapter mSmartBaseTableAdapter;
    private final SmartTableView mSmartTableView;
    private OnBindListener mOnBindListener;
    private SmartBaseTableAdapter.SidebarObserver mSidebarObserver;

    public SmartSidebarAdapter(SmartBaseTableAdapter smartBaseTableAdapter, SmartTableView smartTableView) {
        mSmartBaseTableAdapter = smartBaseTableAdapter;
        mSmartTableView = smartTableView;
        mSidebarObserver = new SmartBaseTableAdapter.SidebarObserver() {
            @Override
            public void onSidebarItemChanged(int position, boolean changeSize) {
                if (changeSize) {
                    mSmartTableView.getHeightSparseArray().delete(position);
                    if (mSmartTableView.getContentRecyclerView().getAdapter() != null) {
                        mSmartTableView.getContentRecyclerView().getAdapter().notifyItemChanged(position);
                    }
                } else {
                    notifyItemChanged(position);
                }

            }
        };
        smartBaseTableAdapter.setSidebarObserver(mSidebarObserver);
    }


    @NonNull
    @Override
    public SmartSidebarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mSmartBaseTableAdapter.onCreateSidebarViewHolder(parent, viewType);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void onBindViewHolder(@NonNull SmartSidebarViewHolder holder, int position) {

        mSmartBaseTableAdapter.onBindSidebarViewHolder(holder, position);
        if (mSmartTableView.getHeightSparseArray().get(position) != 0) {

            holder.itemView.getLayoutParams().height = mSmartTableView.getHeightSparseArray().get(position);

        }
        if (mOnBindListener != null) {
            mOnBindListener.onBind(position);
        } else if (mSmartTableView.getSidebarNullLayoutManagerHolder().get(position)) {
            SmartContentAdapter adapter = (SmartContentAdapter) mSmartTableView.getContentRecyclerView().getAdapter();
            if (adapter != null) {
                adapter.notifyItemChanged(position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mSmartBaseTableAdapter.getRowCount();
    }

    public void setOnBindListener(OnBindListener onBindListener) {
        mOnBindListener = onBindListener;
    }
}
