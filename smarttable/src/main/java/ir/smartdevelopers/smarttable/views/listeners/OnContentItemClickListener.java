package ir.smartdevelopers.smarttable.views.listeners;


import ir.smartdevelopers.smarttable.views.BaseSmartContentItemViewHolder;

public interface OnContentItemClickListener {
    void onContentItemClicked(BaseSmartContentItemViewHolder contentItem, int rowPosition, int colPosition);
}
