package ir.smartdevelopers.smarttable.views.listeners;

import ir.smartdevelopers.smarttable.views.BaseSmartHeaderItemViewHolder;

public interface OnHeaderItemLongClickListener {
    boolean onHeaderItemClicked(BaseSmartHeaderItemViewHolder headerItem, int position);
}
