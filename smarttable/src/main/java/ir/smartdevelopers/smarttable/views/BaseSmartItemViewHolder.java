package ir.smartdevelopers.smarttable.views;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TableRow;

import androidx.annotation.Px;

public abstract class BaseSmartItemViewHolder {
    private View mItemView;
    private Object tag;
    private ViewParent mParent;
    private int mPosition;
    private ViewGroup.LayoutParams mDefaultLayoutParams;

    public BaseSmartItemViewHolder(View itemView) {
        mItemView = itemView;
    }

    protected void setLayoutSize(int width,int height){
        getItemView().setLayoutParams(new ViewGroup.LayoutParams(width, height));
    }

    /**
     * This method used by {@link SmartTableView} to change width of all cells according to
     * maximum width of cells in the column
     * If you want change default behavior of cell sizing override this method
     * */
    protected  void setLayoutWidth(@Px int width){
        ViewGroup.LayoutParams params=getItemView().getLayoutParams();
        params.width=width;
        getItemView().setLayoutParams(params);
    }
    /**
     * This method used by {@link SmartTableView} to change height of all cells according to
     * maximum height of cells in the row
     * If you want change default behavior of cell sizing override this method
     * */
    protected  void setLayoutHeight(@Px int height){
        ViewGroup.LayoutParams params=getItemView().getLayoutParams();
        params.height=height;
        getItemView().setLayoutParams(params);
    }
    /**
     * Returns the table cell width
     * */
    protected  int getLayoutWidth(){
        return getItemView().getWidth();
    }
    /**
     * Returns the table cell height
     * */
    protected  int getLayoutHeight(){
        return getItemView().getHeight();
    }

    public View getItemView(){
        return mItemView;
    }
    public void setBackground(Drawable drawable){
        getItemView().setBackground(drawable);
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public ViewParent getParent() {
        return mParent;
    }

    public void setParent(ViewParent parent) {
        mParent = parent;
    }

    /**
     * Returns the position of the cell in its part
     * */
    public int getAdapterPosition() {
        return mPosition;
    }

    protected void setAdapterPosition(int position) {
        mPosition = position;
    }

     ViewGroup.LayoutParams getDefaultLayoutParams() {
        return mDefaultLayoutParams;
    }

     void setDefaultLayoutParams(ViewGroup.LayoutParams defaultLayoutParams) {
        mDefaultLayoutParams = defaultLayoutParams;
    }
}
