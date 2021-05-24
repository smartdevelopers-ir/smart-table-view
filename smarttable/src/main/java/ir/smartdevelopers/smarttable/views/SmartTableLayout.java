package ir.smartdevelopers.smarttable.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;

public class SmartTableLayout  extends LinearLayout {
    private boolean mInitialize=false;
    public SmartTableLayout(Context context) {
        super(context);
        initialize();

    }

    public SmartTableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();

    }
    private void initialize(){
        setOrientation(LinearLayout.VERTICAL);
        mInitialize=true;
    }


    public void requestAllView(){
        if (mInitialize) {
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                getChildAt(i).requestLayout();
            }
        }
    }
}
