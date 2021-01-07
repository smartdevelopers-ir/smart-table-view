package ir.smartdevelopers.smarttable.views.TableView2;

import android.util.SparseIntArray;

import androidx.collection.SparseArrayCompat;

 class SparseUtil {
    public static int getMax(SparseIntArray sparseIntArray){
        int max=0;
        int arraySize=sparseIntArray.size();
        for (int i=0;i<arraySize;i++){
            if (sparseIntArray.get(sparseIntArray.keyAt(i))>max){
                max=sparseIntArray.get(sparseIntArray.keyAt(i));
            }
        }

        return max;
    }
}
