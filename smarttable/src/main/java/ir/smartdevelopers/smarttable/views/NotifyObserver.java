package ir.smartdevelopers.smarttable.views;

public interface NotifyObserver {
    void notifyDataSetChanged();
    void notifyContentItemChanged(int adapterPosition);
    void notifyContentItemChanged(int rowPosition,int columnPosition);
    void notifyHeaderItemChanged(int position);
    void notifySidebarItemChanged(int position);
}
