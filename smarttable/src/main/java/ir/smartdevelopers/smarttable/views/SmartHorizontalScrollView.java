package ir.smartdevelopers.smarttable.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import androidx.annotation.RequiresApi;

public class SmartHorizontalScrollView extends HorizontalScrollView {
    private  int endScrollX =0;
    private int currentScrollX=0;
    private ir.smartdevelopers.smarttable.views.listeners.OnScrollChangeListener mOnScrollChangeListener;
    public SmartHorizontalScrollView(Context context) {
        super(context);
        setSmoothScrollingEnabled(false);
    }

    public SmartHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSmoothScrollingEnabled(false);
    }

    public SmartHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSmoothScrollingEnabled(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SmartHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setSmoothScrollingEnabled(false);

    }

    @Override
    public void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);

        if (mOnScrollChangeListener!=null){
            mOnScrollChangeListener.onScrollChanged(this,x,y,oldX,oldY);
        }
    }

    public void setOnScrollChangeListener(ir.smartdevelopers.smarttable.views.listeners.OnScrollChangeListener onScrollChangeListener) {
        mOnScrollChangeListener = onScrollChangeListener;
    }

    @Override
    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);
        int childWidth=getChildAt(0).getWidth();
        currentScrollX=getScrollX();
        if (getLayoutDirection()==LAYOUT_DIRECTION_RTL) {
            /* if can scroll*/
            if (childWidth>getWidth()) {
                /* if not scrolled yet scroll to start
                * and if scrolled by user to any position
                * when layout changed do not scroll again to start
                 */
                if (endScrollX==0 || (currentScrollX==endScrollX)) {
                    fullScroll(FOCUS_RIGHT);

                }
            }
            endScrollX =getScrollX();
        }

    }


}
