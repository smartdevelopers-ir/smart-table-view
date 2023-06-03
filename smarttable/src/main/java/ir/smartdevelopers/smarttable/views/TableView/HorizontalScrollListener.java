package ir.smartdevelopers.smarttable.views.TableView;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class HorizontalScrollListener extends RecyclerView.OnScrollListener implements RecyclerView.OnItemTouchListener {

    private SmartRecyclerView mCurrentRVTouched;
    private final SmartRecyclerView mContentRV;
    private final SmartRecyclerView mHeaderRv;
    private final SmartTableView mSmartTableView;
    private RecyclerView mLastTouchedRv;
    private int mXPosition;
    private boolean mIsMoving;
    private int mScrollPosition;
    private final RecyclerView.LayoutManager mContentLayoutManager;
    private int mScrollPositionOffset = 0;
    private final VerticalScrollListener mVerticalScrollListener;
    private boolean mIsHorizontallyMoving = false;

    public HorizontalScrollListener(SmartTableView smartTableView) {
        mSmartTableView = smartTableView;
        mContentRV = mSmartTableView.getContentRecyclerView();
        mHeaderRv = mSmartTableView.getHeaderRecyclerView();
        mContentLayoutManager = mContentRV.getLayoutManager();
        mVerticalScrollListener = smartTableView.getVerticalScrollListener2();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        // Prevent multitouch, once we start to listen with a RV,
        // we ignore any other RV until the touch is released (UP)
        if (mCurrentRVTouched != null && rv != mCurrentRVTouched) {
            return true;
        }
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            mCurrentRVTouched = (SmartRecyclerView) rv;
            if (rv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                if (mLastTouchedRv != null && rv != mLastTouchedRv) {
                    if (mLastTouchedRv == mHeaderRv) {
                        mHeaderRv.removeOnScrollListener(this);
                        mHeaderRv.stopScroll();
                    }
                } else {
                    int lastTouchedIndex = getLastTouchedIndex(rv);

                    if (mContentLayoutManager == null) {
                        return false;
                    }
                    if (lastTouchedIndex >= 0 && lastTouchedIndex < mContentLayoutManager.getChildCount()) {
                        ((SmartRecyclerView) mContentLayoutManager.getChildAt(lastTouchedIndex)).removeOnScrollListener(this);
                        ((SmartRecyclerView) mContentLayoutManager.getChildAt(lastTouchedIndex)).stopScroll();
                    }

                }
                mXPosition = ((SmartRecyclerView) rv).getScrolledX();

            }

        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            mCurrentRVTouched = (SmartRecyclerView) rv;
            rv.addOnScrollListener(this);
            mIsMoving = true;
        } else if (e.getAction() == MotionEvent.ACTION_UP) {

            mCurrentRVTouched = null;
            int nScrollX = ((SmartRecyclerView) rv).getScrolledX();
            if (mXPosition == nScrollX || !mIsMoving) {
                rv.removeOnScrollListener(this);
            }
            mLastTouchedRv = rv;
        } else if (e.getAction() == MotionEvent.ACTION_CANCEL) {
            stopHorizontalScroll();
            rv.removeOnScrollListener(this);
            renewScrollPosition(rv);
            mIsMoving = false;
            mLastTouchedRv = rv;
            mCurrentRVTouched = null;

        }
        return false;
    }

    private void stopHorizontalScroll() {
        if (mSmartTableView.headerIsShowing()) {
            mHeaderRv.stopScroll();
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
        int contentRVCount = mContentLayoutManager.getChildCount();

        if (recyclerView == mHeaderRv) {

            super.onScrolled(recyclerView, dx, dy);
            for (int i = 0; i < contentRVCount; i++) {
                SmartRecyclerView smartRecyclerView = (SmartRecyclerView) mContentLayoutManager.getChildAt(i);
                if (smartRecyclerView != null)
                    smartRecyclerView.scrollBy(dx, 0);
            }
        } else {

            super.onScrolled(recyclerView, dx, dy);

            for (int i = 0; i < contentRVCount; i++) {
                SmartRecyclerView smartRecyclerView = (SmartRecyclerView) mContentLayoutManager.getChildAt(i);
                if (smartRecyclerView != null && smartRecyclerView != recyclerView) {
                    smartRecyclerView.scrollBy(dx, 0);
                }
            }

            if (mSmartTableView.headerIsShowing()) {
                mHeaderRv.scrollBy(dx, 0);
            }
        }


    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            recyclerView.removeOnScrollListener(this);
            renewScrollPosition(recyclerView);
            mVerticalScrollListener.removeScrollListener(true);
            mIsMoving = false;
        }
        mIsHorizontallyMoving = newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING;
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
                mScrollPosition = mScrollPosition + 1;
            }
        }

        boolean isRTL = layoutManager.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        if (isRTL) {
            int sidebarEdgeLeft = 0;
            if (mSmartTableView.sideBarIsShowing()) {
                sidebarEdgeLeft = mSmartTableView.getSidebarRecyclerView().getLeft();
            } else {
                sidebarEdgeLeft = mSmartTableView.getContentRecyclerView().getRight();
            }
            View child = layoutManager.findViewByPosition(mScrollPosition);
            if (child != null) {
                int rightOffset = child.getRight();

                if (recyclerView.getItemDecorationCount() == 1) {
                    Rect rect = new Rect();
                    recyclerView.getDecoratedBoundsWithMargins(child, rect);
                    int dividerWidth = Math.abs(rect.left - child.getLeft());
                    mScrollPositionOffset = Math.abs(sidebarEdgeLeft - (rightOffset + dividerWidth));
                } else {
                    mScrollPositionOffset = Math.abs(sidebarEdgeLeft - rightOffset);
                }

            } else {
                mScrollPositionOffset = 0;
            }
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

    public boolean isMoving() {
        return mIsHorizontallyMoving;
    }

}
