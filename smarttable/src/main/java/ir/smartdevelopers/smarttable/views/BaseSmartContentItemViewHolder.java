package ir.smartdevelopers.smarttable.views;

import android.view.View;

public  class BaseSmartContentItemViewHolder extends BaseSmartItemViewHolder {
    private int columnPosition;
    private int rowPosition;

    public BaseSmartContentItemViewHolder(View view) {
        super(view);
    }

    protected void setContentPosition(int rowPosition,int columnPosition){
        this.rowPosition=rowPosition;
        this.columnPosition=columnPosition;
    }
    /**
     * Returns the column position of the cell in content
     * */
    public int getColumnPosition() {
        return columnPosition;
    }
    /**
     * Returns the row position of the cell in content
     * */
    public int getRowPosition() {
        return rowPosition;
    }
}
