package ir.smartdevelopers.smarttable.views.TableView2;

import android.util.SparseIntArray;
import android.view.View;

public class SmartTableUtil {
    public static int measureWidth(View view){
        int widthSpec= View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightSpec= View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec,heightSpec);
        return view.getMeasuredWidth();
    }
    public static int measureHeight(View view){
        int widthSpec= View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightSpec= View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec,heightSpec);
        return view.getMeasuredHeight();
    }

    public static SparseIntArray getAverageWidth(SmartTableView2 smartTableView2){
        int sidebarWidth=0;
        SparseIntArray tempWidthSize=new SparseIntArray();
        SparseIntArray widthSize=new SparseIntArray();
        if (smartTableView2.sideBarIsShowing()){
            sidebarWidth=SparseUtil.getMax(smartTableView2.getSidebarLayoutManager().getCellsWidthHolder());

        }
        int deviceWidth=smartTableView2.getContext().getResources().getDisplayMetrics().widthPixels;
        int contentWidth=Math.abs(deviceWidth-sidebarWidth);
        int averageWidth=contentWidth/smartTableView2.getAdapter().getColumnCount();
        int colCount=smartTableView2.getAdapter().getColumnCount();
        for (int i=0;i<colCount;i++){
            widthSize.put(i,averageWidth);
        }
        return widthSize;
    }
     static SparseIntArray getStretchedWidth(SmartTableView2 smartTableView2){
         int sidebarWidth=0;
         SparseIntArray tempWidthSize=new SparseIntArray();
         SparseIntArray widthSize=new SparseIntArray();
        if (smartTableView2.sideBarIsShowing()){
            sidebarWidth=SmartTableUtil.measureWidth(smartTableView2.getSidebarRecyclerView());

        }
        int deviceWidth=smartTableView2.getContext().getResources().getDisplayMetrics().widthPixels;
        int contentWidth=Math.abs(deviceWidth-sidebarWidth);
        contentWidth=contentWidth-smartTableView2.getContentMarginStart();
        int colCount=smartTableView2.getAdapter().getColumnCount();

        for (int i=0;i<colCount;i++){
            int maxColWidth=smartTableView2.getAdapter().getMaxColumnWidth(i);
            widthSize.put(i,maxColWidth);
            tempWidthSize.put(i,maxColWidth);
        }
         SmartTableUtil.StretchHelper helper=new SmartTableUtil.StretchHelper();
         helper.mustLooping=true;
         helper.count=tempWidthSize.size();
         helper.totalWidth=contentWidth;
         do {
             SmartTableUtil.mustRemoveMaxWidth(tempWidthSize, helper);
         }while (helper.mustLooping);

         for (int i=0;i<tempWidthSize.size();i++){
             int value=tempWidthSize.valueAt(i);
             int key=tempWidthSize.keyAt(i);
             widthSize.put(key,(int) (value+(helper.average -value)));
         }
        return widthSize;
    }
   public static StretchHelper mustRemoveMaxWidth(SparseIntArray intArray,StretchHelper helper){
        float average=helper.totalWidth/helper.count;
        int max=SparseUtil.getMax(intArray);
        if (average<max && intArray.size()>0){
            intArray.removeAt(intArray.indexOfValue(max));
            helper.mustLooping=true;
        }else {
            helper.mustLooping=false;
        }
        helper.totalWidth=helper.totalWidth-max;
        helper.count=intArray.size();
        helper.average =average;
        return helper;
   }
   public static class StretchHelper{
        public float totalWidth;
        public int count;
        public boolean mustLooping;
        float average;
   }
}
