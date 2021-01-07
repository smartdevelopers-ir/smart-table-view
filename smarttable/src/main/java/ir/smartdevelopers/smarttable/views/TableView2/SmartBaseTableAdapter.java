package ir.smartdevelopers.smarttable.views.TableView2;

import android.view.ViewGroup;

public abstract class SmartBaseTableAdapter<SHV extends SmartHeaderViewHolder,
        SSV extends SmartSidebarViewHolder,SCV extends SmartContentViewHolder> {
    private HeaderObserver mHeaderObserver;
    private SidebarObserver mSidebarObserver;
    private ContentObserver mContentObserver;
    private notifyObserver mNotifyObserver;

    public abstract SHV onCreateHeaderViewHolder(ViewGroup parent,int viewType);
    public abstract SSV onCreateSidebarViewHolder(ViewGroup parent,int viewType);
    public abstract SCV onCreateContentViewHolder(ViewGroup parent,int viewType);
    public abstract void onBindHeaderViewHolder(SHV headerHolder,int position);
    public abstract void onBindSidebarViewHolder(SSV sidebarHolder, int position);
    public abstract void onBindContentViewHolder(SCV contentHolder,int adapterPosition,int rowPosition,int columnPosition);
    public abstract int getRowCount();
    public abstract int getColumnCount();
    public abstract int getContentCount();
    protected abstract int getMaxColumnWidth(int position);
    public final void notifyHeaderItemChanged(int position,boolean changeSize){
         if (mHeaderObserver!=null){
             mHeaderObserver.onHeaderItemChanged(position,changeSize);
         }
     }
    public final void notifySidebarItemChanged(int position,boolean changeSize){
        if (mSidebarObserver!=null){
            mSidebarObserver.onSidebarItemChanged(position,changeSize);
        }
    }
    public final void notifyContentItemChanged(int position,boolean changeSize){
       int rowPos=position / getColumnCount();
       int colPos=position % getColumnCount();
       notifyContentItemChanged(rowPos,colPos,changeSize);
    }
    public final void notifyContentItemChanged(int rowPos,int colPos,boolean changeSize){
        if (mContentObserver!=null){
            mContentObserver.onContentItemChanged(rowPos,colPos, changeSize);
        }
    }
    public final void notifyDataSetChanged(){
        mNotifyObserver.dataSetChanged();
    }
     void setHeaderObserver(HeaderObserver headerObserver) {
        mHeaderObserver = headerObserver;
    }

     void setSidebarObserver(SidebarObserver sidebarObserver) {
        mSidebarObserver = sidebarObserver;
    }

     void setContentObserver(ContentObserver contentObserver) {
        mContentObserver = contentObserver;
    }

     void setNotifyObserver(notifyObserver notifyObserver) {
        mNotifyObserver = notifyObserver;
    }

    interface HeaderObserver{
         void onHeaderItemChanged(int position, boolean changeSize);
     }
    interface SidebarObserver{
        void onSidebarItemChanged(int position, boolean changeSize);
    }
    interface ContentObserver{
        void onContentItemChanged(int rowPos, int colPos, boolean changeSize);
    }
    interface notifyObserver{
         void dataSetChanged();
    }
}
