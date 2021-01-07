package ir.smartdevelopers.smarttable.views.TableView2;

import android.os.Parcelable;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("rawtypes")
 final class SmartContentAdapter extends RecyclerView.Adapter<SmartContentAdapter.ContentRowViewHolder> implements
OnBindListener{

    private SmartBaseTableAdapter mSmartBaseTableAdapter;
    private RecyclerView.RecycledViewPool mRecycledViewPool=new RecyclerView.RecycledViewPool();
    private RecyclerView.OnItemTouchListener mOnItemTouchListener;
    private SmartVerticalLayoutManager mSmartVerticalLayoutManager;
    private SmartVerticalLayoutManager mSidebarLayoutManager;
    private SmartTableView2 mSmartTableView2;


    private SmartBaseTableAdapter.ContentObserver mContentObserver;

    public SmartContentAdapter(final SmartBaseTableAdapter smartBaseTableAdapter, final SmartTableView2 smartTableView2) {
        mSmartBaseTableAdapter = smartBaseTableAdapter;
        mSmartVerticalLayoutManager =smartTableView2.getSidebarLayoutManager();
        mSidebarLayoutManager=smartTableView2.getSidebarLayoutManager();
        mSmartTableView2=smartTableView2;
        mContentObserver=new SmartBaseTableAdapter.ContentObserver() {
            @Override
            public void onContentItemChanged(int rowPos, int colPos, boolean changeSize) {
               SmartVerticalLayoutManager manager= (SmartVerticalLayoutManager) mSmartTableView2.getContentRecyclerView().getLayoutManager();
               if (manager==null){return;}
               if (changeSize){

                   mSmartTableView2.getHeightSparseArray().delete(rowPos);
                   mSmartTableView2.getWidthIntArray().delete(colPos);
                   if (mSmartTableView2.headerIsShowing()){
                       Parcelable recyclerViewState=smartTableView2.getHeaderLayoutManager().onSaveInstanceState();
                       //noinspection ConstantConditions
                       mSmartTableView2.getHeaderRecyclerView().getAdapter().notifyItemChanged(colPos);

                       smartTableView2.getHeaderLayoutManager().onRestoreInstanceState(recyclerViewState);
                   }
                   for (int i=0;i<smartBaseTableAdapter.getRowCount();i++){
                       if (i==rowPos){
                           continue;
                       }
                       smartBaseTableAdapter.notifyContentItemChanged(i,colPos,false);
                   }
               }
                View rowView=manager.findViewByPosition(rowPos);
                if (rowView instanceof SmartRecyclerView){
                    RecyclerView.Adapter adapter=((SmartRecyclerView) rowView).getAdapter();
                    if (adapter!=null){
                        adapter.notifyItemChanged(colPos);
                    }
                }

            }
        };
        smartBaseTableAdapter.setContentObserver(mContentObserver);
    }

    @NonNull
    @Override
    public ContentRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SmartRecyclerView innerRV=new SmartRecyclerView(parent.getContext());
        innerRV.setRecycledViewPool(mRecycledViewPool);
//        innerRV.setItemAnimator(new TableItemChangeAnimator());

        innerRV.setItemAnimator(null);
        innerRV.setLayoutDirection(parent.getLayoutDirection());
        if (mSmartTableView2.getContentColumnItemDecoration()!=null){
            innerRV.addItemDecoration(mSmartTableView2.getContentColumnItemDecoration());
        }
        innerRV.setLayoutManager(new SmartHorizontalContentLayoutManager(parent.getContext(),RecyclerView.HORIZONTAL,false,
                mSmartTableView2));
        if (mOnItemTouchListener!=null) {
            innerRV.addOnItemTouchListener(mOnItemTouchListener);
        }
//        Log.v("TTT","onCreateViewHolder ");
        return new ContentRowViewHolder(innerRV);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentRowViewHolder holder, int position) {
        SmartRecyclerView rowRecyclerView=(SmartRecyclerView)holder.itemView;
        SmartHorizontalAdapter horizontalAdapter=new SmartHorizontalAdapter(mSmartTableView2.getAdapter(),position,
                mSmartTableView2);
        if (mSmartTableView2.getHeightSparseArray().get(position)!=0){
            rowRecyclerView.setHasFixedSize(true);
        }else {
            rowRecyclerView.setHasFixedSize(false);
        }

        rowRecyclerView.setAdapter(horizontalAdapter);
        if (mSmartTableView2.sideBarIsShowing()) {
            int contentRowHeight = SmartTableUtil.measureHeight(rowRecyclerView);
            int sidebarHeight = mSidebarLayoutManager.getCellsWidthHolder().get(position);
            if (sidebarHeight == 0) {
                View sidebar = mSidebarLayoutManager.findViewByPosition(position);
                if (sidebar != null ) {
                    sidebarHeight = SmartTableUtil.measureHeight(sidebar);
                }else {
                    mSmartTableView2.getSidebarNullLayoutManagerHolder().put(position,true);
                }
            }

            int maxHeight = Math.max(contentRowHeight, sidebarHeight);
            if (sidebarHeight!=0)
            mSmartTableView2.getHeightSparseArray().put(position,maxHeight);
            if (maxHeight>sidebarHeight){
                // notify sidebar item changed
                SmartSidebarAdapter sidebarAdapter=(SmartSidebarAdapter)  mSmartTableView2.getSidebarRecyclerView().getAdapter();
                if (sidebarAdapter != null) {
                    sidebarAdapter.notifyItemChanged(position);
                }
            }

            ViewGroup.LayoutParams defaultParams = rowRecyclerView.getLayoutParams();
            if (defaultParams != null) {

                int defaultHeight = defaultParams.height;
                if (defaultHeight != maxHeight) {
                    defaultParams.height = maxHeight;
//                rowRecyclerView.setLayoutParams(defaultParams);
                }
            } else {

//                Log.v("TTT", "content row height= " + contentRowHeight + " sidebar row =" + sidebarHeight);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        maxHeight);
                rowRecyclerView.setLayoutParams(params);
            }



        }

    }



    @Override
    public int getItemCount() {
        return mSmartBaseTableAdapter.getRowCount();
    }

    public void setOnItemTouchListener(RecyclerView.OnItemTouchListener onItemTouchListener) {
        mOnItemTouchListener = onItemTouchListener;
    }

    @Override
    public void onBind(int position) {
//        Log.v("TTT","onBind = "+position);
        if (mSidebarLayoutManager!=null && mSidebarLayoutManager.getCellsWidthHolder().get(position)==0)
        notifyItemChanged(position);
    }


    static class ContentRowViewHolder extends RecyclerView.ViewHolder {

        public ContentRowViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

}
