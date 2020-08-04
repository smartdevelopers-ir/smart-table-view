package ir.smartdevelopers.smarttable.views;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

public abstract class BaseSmartTableAdapter<SVH extends BaseSmartSidebarItemViewHolder,
        HVH extends BaseSmartHeaderItemViewHolder,
        CVH extends BaseSmartContentItemViewHolder> {
    private NotifyObserver mNotifyObserver;


    public abstract int getRowCount();
    public abstract int getColumnCount();
    public abstract int getContentItemCount();

    protected abstract SVH onCreateSidebarViewHolder( ViewGroup parent);
    protected abstract HVH onCreateHeaderViewHolder( ViewGroup parent);
    protected abstract CVH onCreateContentViewHolder( ViewGroup parent);

    public abstract void onBindSidebarViewHolder(SVH sidebarHolder,
                                                                 int position);

    public abstract void onBindHeaderViewHolder(HVH headerHolder,int position);

    public abstract void onBindContentViewHolder(CVH contentHolder,int rowPosition, int columnPosition) ;

    protected void setNotifyObserver(NotifyObserver observer){
        this.mNotifyObserver=observer;
    }
    public void notifyDataSetChanged(){
        mNotifyObserver.notifyDataSetChanged();
    }

    public void notifyContentItemChanged(int adapterPosition){
        mNotifyObserver.notifyContentItemChanged(adapterPosition);
    };
    public void notifyContentItemChanged(int rowPosition,int columnPosition){
        mNotifyObserver.notifyContentItemChanged(rowPosition,columnPosition);
    };
    public void notifyHeaderItemChanged(int position){
        mNotifyObserver.notifyHeaderItemChanged(position);
    };
    public void notifySidebarItemChanged(int position){
        mNotifyObserver.notifySidebarItemChanged(position);
    };
    /**
     * Some times you want to change hole content row background .At this time you must override this method
     * and return a drawable as background you want
     * @param rowPosition position of current row
     * */
    public Drawable getContentRowBackground(int rowPosition){
        return null;
    }
    /**
     * Some times you want to change hole sidebar row background .At this time you must override this method
     * and return a drawable as background you want
     * @param rowPosition position of current row
     * */
    public Drawable getSidebarRowBackground(int rowPosition){
        return null;
    }




}
