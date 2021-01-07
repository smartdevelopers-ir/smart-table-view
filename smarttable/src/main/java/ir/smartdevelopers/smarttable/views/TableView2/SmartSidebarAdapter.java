package ir.smartdevelopers.smarttable.views.TableView2;

import android.util.Pair;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("rawtypes")
final class SmartSidebarAdapter extends RecyclerView.Adapter<SmartSidebarViewHolder> {
    private SmartBaseTableAdapter mSmartBaseTableAdapter;
    private SmartTableView2 mSmartTableView2;
    private OnBindListener mOnBindListener;
    private SmartBaseTableAdapter.SidebarObserver mSidebarObserver;

    public SmartSidebarAdapter(SmartBaseTableAdapter smartBaseTableAdapter, SmartTableView2 smartTableView2) {
        mSmartBaseTableAdapter = smartBaseTableAdapter;
        mSmartTableView2 = smartTableView2;
        mSidebarObserver=new SmartBaseTableAdapter.SidebarObserver() {
            @Override
            public void onSidebarItemChanged(int position, boolean changeSize) {
                if (changeSize){
                    mSmartTableView2.getHeightSparseArray().delete(position);
                    if (mSmartTableView2.getContentRecyclerView().getAdapter()!=null){
                        mSmartTableView2.getContentRecyclerView().getAdapter().notifyItemChanged(position);
                    }
                }else {
                    notifyItemChanged(position);
                }

            }
        };
        smartBaseTableAdapter.setSidebarObserver(mSidebarObserver);
    }


    @NonNull
    @Override
    public SmartSidebarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mSmartBaseTableAdapter.onCreateSidebarViewHolder(parent,viewType);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    public void onBindViewHolder(@NonNull SmartSidebarViewHolder holder, int position) {

        mSmartBaseTableAdapter.onBindSidebarViewHolder(holder,position);
        if (mSmartTableView2.getHeightSparseArray().get(position)!=0){

            holder.itemView.getLayoutParams().height = mSmartTableView2.getHeightSparseArray().get(position);

        }
        if (mOnBindListener!=null){
            mOnBindListener.onBind(position);
        }else if (mSmartTableView2.getSidebarNullLayoutManagerHolder().get(position)){
            SmartContentAdapter adapter=(SmartContentAdapter) mSmartTableView2.getContentRecyclerView().getAdapter();
            if (adapter!=null){
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
