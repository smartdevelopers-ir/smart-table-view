package ir.smartdevelopers.smarttable.views.TableView;

import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

final class VerticalScrollListener extends RecyclerView.OnScrollListener implements RecyclerView.OnItemTouchListener {

    private SmartRecyclerView mCurrentRVTouched;
    private final SmartRecyclerView mContentRV;
    private final SmartRecyclerView mSidebarRv;
    private final SmartTableView mSmartTableView;
    private RecyclerView mLastTouchedRv;
    private int mYPosition;
    private boolean mIsMoving;
    private int mScrollPosition;
    private final RecyclerView.LayoutManager mContentLayoutManager;
    private int mScrollPositionOffset = 0;

    public VerticalScrollListener(SmartTableView smartTableView) {
        mSmartTableView = smartTableView;
        mContentRV = mSmartTableView.getContentRecyclerView();
        mSidebarRv = mSmartTableView.getSidebarRecyclerView();
        mContentLayoutManager = mContentRV.getLayoutManager();
    }

    private float dx = 0, dy = 0;

    /**
     * check which direction the user is scrolling
     *
     * @param ev
     * @return
     */
    private boolean verticalDirection(@NonNull MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (dx == 0) {
                dx = ev.getX();
            }
            if (dy == 0) {
                dy = ev.getY();
            }
            float xdiff = Math.abs(dx - ev.getX());
            float ydiff = Math.abs(dy - ev.getY());
            dx = ev.getX();
            dy = ev.getY();

            if (xdiff > ydiff) {
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        // Prevent multitouch, once we start to listen with a RV,
        // we ignore any other RV until the touch is released (UP)
        if (mCurrentRVTouched != null && rv != mCurrentRVTouched) {
            return true;
        }

        // If scroll direction is not Vertical, then ignore and reset last RV touched
        if (!verticalDirection(e)) {
            mCurrentRVTouched = null;
            return false;
        } else {
            stopHorizontalScroll();
        }
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            mCurrentRVTouched = (SmartRecyclerView) rv;
            if (rv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                if (mLastTouchedRv != null && rv != mLastTouchedRv) {
                    if (mLastTouchedRv == mSidebarRv) {
                        mSidebarRv.removeOnScrollListener(this);
                        mSidebarRv.stopScroll();
                        if (mSmartTableView.sideBarIsShowing()) {
                            SmartSidebarAdapter sidebarAdapter = (SmartSidebarAdapter) mSidebarRv.getAdapter();
                            sidebarAdapter.setOnBindListener((OnBindListener) mContentRV.getAdapter());
                        }
                    } else if (mLastTouchedRv == mContentRV) {
                        mContentRV.removeOnScrollListener(this);
                        mContentRV.stopScroll();
                        if (mSmartTableView.sideBarIsShowing()) {
                            SmartSidebarAdapter sidebarAdapter = (SmartSidebarAdapter) mSidebarRv.getAdapter();
                            sidebarAdapter.setOnBindListener(null);
                        }
                    }
                }
                if (mCurrentRVTouched == mContentRV && mSmartTableView.sideBarIsShowing()) {
                    SmartSidebarAdapter sidebarAdapter = (SmartSidebarAdapter) mSidebarRv.getAdapter();
                    sidebarAdapter.setOnBindListener((OnBindListener) mContentRV.getAdapter());
                }
                mYPosition = ((SmartRecyclerView) rv).getScrolledY();
                rv.addOnScrollListener(this);
            }

        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            mCurrentRVTouched = (SmartRecyclerView) rv;
            mIsMoving = true;
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            mCurrentRVTouched = null;
            int nScrollY = ((SmartRecyclerView) rv).getScrolledY();
            if (mYPosition == nScrollY && !mIsMoving && rv.getScrollState() == RecyclerView
                    .SCROLL_STATE_IDLE) {
                rv.removeOnScrollListener(this);

            }
            mLastTouchedRv = rv;
        } else if (e.getAction() == MotionEvent.ACTION_CANCEL) {
            rv.removeOnScrollListener(this);
            mIsMoving = false;
            mLastTouchedRv = rv;
            mCurrentRVTouched = null;

        }
        return false;
    }


    public void removeScrollListener(boolean isNeeded) {
        if (mLastTouchedRv == mContentRV) {
            mContentRV.removeOnScrollListener(this);
            mContentRV.stopScroll();
        } else {
            if (mSmartTableView.sideBarIsShowing()) {
                mSidebarRv.removeOnScrollListener(this);
                mSidebarRv.stopScroll();
            }
            if (isNeeded) {
                mContentRV.removeOnScrollListener(this);
                mContentRV.stopScroll();

            }
        }

    }

    private void stopHorizontalScroll() {
        if (mSmartTableView.headerIsShowing()) {
            mSmartTableView.getHeaderRecyclerView().stopScroll();
        }
        for (int i = 0; i < mContentLayoutManager.getChildCount(); i++) {
            View view = mContentLayoutManager.getChildAt(i);
            if (view instanceof SmartRecyclerView) {
                ((SmartRecyclerView) view).stopScroll();
            }
        }
    }

    private int getLastTouchedIndex(RecyclerView rv) {

        if (mContentLayoutManager == null) {
            return -1;
        }
        ;
        int count = mContentLayoutManager.getChildCount();
        for (int i = 0; i < count; i++) {
            if (mContentLayoutManager.getChildAt(i) == rv) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

        if (mContentLayoutManager == null) {
            return;
        }

        if (recyclerView == mSidebarRv) {
            super.onScrolled(recyclerView, dx, dy);
            mContentRV.scrollBy(0, dy);
        } else {
            if (mSmartTableView.sideBarIsShowing()) {
                mSidebarRv.scrollBy(0, dy);
            }
            super.onScrolled(recyclerView, dx, dy);
        }


    }


    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            recyclerView.removeOnScrollListener(this);
            renewScrollPosition(recyclerView);
            mCurrentRVTouched = null;
            mIsMoving = false;
        }
    }

    /**
     * This method calculates the current scroll position and its offset to help new attached
     * recyclerView on window at that position and offset
     */
    private void renewScrollPosition(@NonNull RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        mScrollPosition = layoutManager.findFirstCompletelyVisibleItemPosition();

        // That means there is no completely visible Position.
        if (mScrollPosition == -1) {
            mScrollPosition = layoutManager.findFirstVisibleItemPosition();

            // That means there is just a visible item on the screen
            if (mScrollPosition == layoutManager.findLastVisibleItemPosition()) {
                // in this case we use the position which is the last & first visible item.
            } else {
                // That means there are 2 visible item on the screen. However, second one is not
                // completely visible.
//                mScrollPosition = mScrollPosition + 1;
            }
        }

        boolean isRTL = layoutManager.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        if (isRTL) {
            mScrollPositionOffset = layoutManager.findViewByPosition(mScrollPosition).getRight();
        } else {
            mScrollPositionOffset = layoutManager.findViewByPosition(mScrollPosition).getLeft();

        }
    }

    public int getScrollPositionOffset() {
        return mScrollPositionOffset;
    }

    public void setScrollPositionOffset(int scrollPositionOffset) {
        mScrollPositionOffset = scrollPositionOffset;
    }

    public int getScrollPosition() {
        return mScrollPosition;
    }

    public void setScrollPosition(int scrollPosition) {
        mScrollPosition = scrollPosition;
    }
}
