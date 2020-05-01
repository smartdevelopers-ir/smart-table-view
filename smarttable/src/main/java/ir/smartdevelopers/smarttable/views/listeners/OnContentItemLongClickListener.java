package ir.smartdevelopers.smarttable.views.listeners;


import ir.smartdevelopers.smarttable.views.BaseSmartContentItemViewHolder;

public interface OnContentItemLongClickListener {
    boolean onContentItemClicked(BaseSmartContentItemViewHolder contentItem, int rowPosition, int colPosition);
}
