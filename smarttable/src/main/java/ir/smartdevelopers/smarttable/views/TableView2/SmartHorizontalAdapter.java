package ir.smartdevelopers.smarttable.views.TableView2;

import android.util.Pair;
import android.util.SparseIntArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings({"unchecked", "rawtypes"})
final class SmartHorizontalAdapter extends RecyclerView.Adapter<SmartContentViewHolder> {
    private SmartBaseTableAdapter mSmartBaseTableAdapter;
//    private int mColumnPos;
    private int mRowPos;
    private SmartTableView2 mSmartTableView2;
    private SparseIntArray mWidthIntArray=new SparseIntArray();


    public SmartHorizontalAdapter(SmartBaseTableAdapter smartBaseTableAdapter,
                                  int rowPos, SmartTableView2 smartTableView2) {
        mSmartBaseTableAdapter = smartBaseTableAdapter;
//        this.mColumnPos = columnPos;
        this.mRowPos = rowPos;

        mSmartTableView2 = smartTableView2;

    }

    @NonNull
    @Override
    public SmartContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mSmartBaseTableAdapter.onCreateContentViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull SmartContentViewHolder holder, int position) {

        if (mSmartTableView2.isStretchIfNotFit() && !mSmartTableView2.isFitHorizontally()){
            if (mSmartTableView2.getWidthIntArray().get(position)==0) {
                mWidthIntArray = mSmartTableView2.calculateStretchWidth();
            }

        }else if (mSmartTableView2.isFitHorizontally()){
            if (mSmartTableView2.getWidthIntArray().get(position)==0) {
                /*if we have calculated before just set another value o this position*/
                if (position-1>=0) {
                    if (mSmartTableView2.getWidthIntArray().get(position-1)!=0) {
                        mSmartTableView2.getWidthIntArray().put(position,
                                mSmartTableView2.getWidthIntArray().get(position - 1));
                    }else {
                        mWidthIntArray=mSmartTableView2.calculateAverageWidth();
                    }
                }
            }

        }
            mWidthIntArray=mSmartTableView2.getWidthIntArray();

        ViewGroup.LayoutParams params=holder.itemView.getLayoutParams();
        int max=mWidthIntArray.get(position);
        if (max==0){

            max= mSmartBaseTableAdapter.getMaxColumnWidth(position);
            mWidthIntArray.put(position,max);
        }
        params.width=max;

        int adapterPos=mRowPos * mSmartBaseTableAdapter.getColumnCount()+position;
        int colPos=adapterPos % mSmartBaseTableAdapter.getColumnCount();
        holder.setRowPosition(mRowPos);
        holder.setColumnPosition(colPos);
        holder.setContentPosition(adapterPos);
        mSmartBaseTableAdapter.onBindContentViewHolder(holder,adapterPos,mRowPos,colPos);
    }

    @Override
    public int getItemCount() {
        return mSmartBaseTableAdapter.getColumnCount();
    }


}
