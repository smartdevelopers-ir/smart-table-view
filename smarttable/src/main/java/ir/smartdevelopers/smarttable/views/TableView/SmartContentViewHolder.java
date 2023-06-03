package ir.smartdevelopers.smarttable.views.TableView;

import android.view.View;
import androidx.annotation.NonNull;

public abstract class SmartContentViewHolder extends SmartAbstractViewHolder {
    private int rowPosition;
    private int columnPosition;
    private int mContentPosition;
    public SmartContentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public int getRowPosition() {
        return rowPosition;
    }

     void setRowPosition(int rowPosition) {
        this.rowPosition = rowPosition;
    }

    public int getColumnPosition() {
        return columnPosition;
    }

     void setColumnPosition(int columnPosition) {
        this.columnPosition = columnPosition;
    }


    public int getContentPosition() {
        return mContentPosition;
    }

     void setContentPosition(int contentPosition) {
        mContentPosition = contentPosition;
    }

}
