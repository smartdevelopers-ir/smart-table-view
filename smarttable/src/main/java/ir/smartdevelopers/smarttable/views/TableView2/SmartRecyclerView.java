package ir.smartdevelopers.smarttable.views.TableView2;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

 class SmartRecyclerView extends RecyclerView {

    private int mScrolledX;
    private int mScrolledY;
    private boolean mIsHorizontalScrollListenerRemoved = true;
    private boolean mIsVerticalScrollListenerRemoved = true;

    public SmartRecyclerView(@NonNull Context context) {
        this(context,null);
    }

    public SmartRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SmartRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setHasFixedSize(false);
        setNestedScrollingEnabled(false);
//        setItemViewCacheSize();
        this.setDrawingCacheEnabled(true);
        this.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        mScrolledX +=dx;
        mScrolledY +=dy;
        super.onScrolled(dx, dy);
    }
    public int getScrolledX() {
        return mScrolledX;
    }

    public void setScrolledX(int scrolledX) {
        mScrolledX = scrolledX;
    }

    public int getScrolledY() {
        return mScrolledY;
    }

    public void setScrolledY(int scrolledY) {
        mScrolledY = scrolledY;
    }
    public void clearScrolledX(){
        mScrolledX=0;
    }
    public void clearScrolledY(){
        mScrolledY=0;
    }
    @Override
    public void removeOnScrollListener(@NonNull OnScrollListener listener) {
        if (listener instanceof HorizontalScrollListener) {
            if (mIsHorizontalScrollListenerRemoved) {
                // Do not let remove the listener
                Log.e("TTT", "HorizontalRecyclerViewListener has been tried to remove " +
                        "itself before add new one");
            } else {
                mIsHorizontalScrollListenerRemoved = true;
                super.removeOnScrollListener(listener);
            }
        } else if (listener instanceof VerticalScrollListener2) {
            if (mIsVerticalScrollListenerRemoved) {
                // Do not let remove the listener
                Log.e("TTT", "mIsVerticalScrollListenerRemoved has been tried to remove " +
                        "itself before add new one");
            } else {
                mIsVerticalScrollListenerRemoved = true;
                super.removeOnScrollListener(listener);
            }
        } else {
            super.removeOnScrollListener(listener);
        }
    }
    @Override
    public void addOnScrollListener(@NonNull OnScrollListener listener) {
        if (listener instanceof HorizontalScrollListener) {
            if (mIsHorizontalScrollListenerRemoved) {
                mIsHorizontalScrollListenerRemoved = false;
                super.addOnScrollListener(listener);
            } else {
                // Do not let add the listener
                Log.w("TTT", "mIsHorizontalScrollListenerRemoved has been tried to add itself "
                        + "before remove the old one");
            }
        } else if (listener instanceof VerticalScrollListener2) {
            if (mIsVerticalScrollListenerRemoved) {
                mIsVerticalScrollListenerRemoved = false;
                super.addOnScrollListener(listener);
            } else {
                // Do not let add the listener
                Log.w("TTT", "mIsVerticalScrollListenerRemoved has been tried to add itself " +
                        "before remove the old one");
            }
        } else {
            super.addOnScrollListener(listener);
        }
    }
    public boolean isHorizontalScrollListenerRemoved() {
        return mIsHorizontalScrollListenerRemoved;
    }

    public boolean isScrollOthers() {
        return !mIsHorizontalScrollListenerRemoved;
    }
}
