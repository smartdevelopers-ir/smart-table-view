package ir.smartdevelopers.smarttable.views.TableView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ir.smartdevelopers.smarttable.R;

public class SmartTableView extends ConstraintLayout implements SmartBaseTableAdapter.notifyObserver {

    private FrameLayout mCornerView;
    private SmartRecyclerView mHeaderRecyclerView;
    private SmartHeaderLayoutManager mHeaderLayoutManager;
    private SmartRecyclerView mSidebarRecyclerView;
    private SmartVerticalLayoutManager mSidebarLayoutManager;
    private SmartRecyclerView mContentRecyclerView;
    private SmartVerticalLayoutManager mContentLayoutManager;
    private SparseIntArray horizontalWidthHolder = new SparseIntArray();
    private HorizontalScrollListener horizontalScrollListener;
    private VerticalScrollListener mVerticalScrollListener;
    /*dividers*/
    private RecyclerView.ItemDecoration mHeaderItemDecoration;
    private RecyclerView.ItemDecoration mSidebarItemDecoration;
    private RecyclerView.ItemDecoration mContentRowItemDecoration;
    private RecyclerView.ItemDecoration mContentColumnItemDecoration;
    private int mContentMarginStart;
    private int mContentMarginTop;

    private boolean mShowSideBar;
    private boolean mShowHeader;
    private boolean mStretchIfNotFit;
    private boolean mFitHorizontally;

    private SparseIntArray mWidthIntArray = new SparseIntArray();
    private SparseIntArray mHeightSparseArray = new SparseIntArray();
    /**
     * for preventing calculating stretch Width  multiple time
     */
    private boolean stretchWidthCalculated = false;
    /**
     * for preventing calculating average width multiple time
     */
    private boolean averageWidthCalculated = false;

    private boolean mShowDefaultDividers;
    @SuppressWarnings("rawtypes")
    private SmartBaseTableAdapter mAdapter;
    private final SparseBooleanArray sidebarNullLayoutManagerHolder = new SparseBooleanArray();


    private boolean mMustReMeasure;

    public SmartTableView(@NonNull Context context) {
        super(context);
        init(context, null);

    }

    public SmartTableView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public SmartTableView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    public SmartTableView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.smart_table_layout_2, this, true);
        mHeaderRecyclerView = view.findViewById(R.id.smart_table_view_header_recyclerView);
        mSidebarRecyclerView = view.findViewById(R.id.smart_table_view_sidebar_recyclerView);
        mContentRecyclerView = view.findViewById(R.id.smart_table_view_content_recyclerView);
        mCornerView = view.findViewById(R.id.smart_table_view_corner_top_view);
        mContentMarginStart = context.getResources().getDimensionPixelSize(R.dimen.smart_table_default_vertical_divider_width);
        mContentMarginTop = context.getResources().getDimensionPixelSize(R.dimen.smart_table_default_horizontal_divider_height);
        if (attrs != null) {
            try {
                TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartTableView);
                mShowHeader = typedArray.getBoolean(R.styleable.SmartTableView_showHeader, true);
                setShowHeader(mShowHeader);
                mShowSideBar = typedArray.getBoolean(R.styleable.SmartTableView_showSidebar, true);
                setShowSideBar(mShowSideBar);
                mStretchIfNotFit = typedArray.getBoolean(R.styleable.SmartTableView_stretchIfContentNotFitScreen, false);
//            setStretchIfNotFit(mStretchIfNotFit);
                mFitHorizontally = typedArray.getBoolean(R.styleable.SmartTableView_fitHorizontally, false);
//            setFitHorizontally(mFitHorizontally);
                mShowDefaultDividers = typedArray.getBoolean(R.styleable.SmartTableView_showDefaultDividers, false);
                if (mShowDefaultDividers) {
                    /*column dividers*/
                    DefaultTableVerticalDivider headerItemDecoration = new DefaultTableVerticalDivider(context, DividerItemDecoration.HORIZONTAL);
                    DefaultTableVerticalDivider contentColumnItemDecoration = new DefaultTableVerticalDivider(context, DividerItemDecoration.HORIZONTAL);
                    addColumnItemDecoration(headerItemDecoration, contentColumnItemDecoration);

                    /*row dividers*/
                    DefaultTableHorizontalDivider sidebarItemDecoration = new DefaultTableHorizontalDivider(context, DividerItemDecoration.VERTICAL);
                    DefaultTableHorizontalDivider contentRowItemDecoration = new DefaultTableHorizontalDivider(context, DividerItemDecoration.VERTICAL);
                    addRowItemDecoration(sidebarItemDecoration, contentRowItemDecoration);

                }

                int cornerViewResourceId = typedArray.getResourceId(R.styleable.SmartTableView_cornerTopRightView, R.layout.smart_table_view_default_corner_view);
                mCornerView.addView(LayoutInflater.from(context).inflate(cornerViewResourceId, mCornerView, false));
                int cornerViewColor = typedArray.getColor(R.styleable.SmartTableView_cornerTopRightColor, 0);
                if (cornerViewColor != 0) {
                    mCornerView.setBackgroundColor(cornerViewColor);
                }
                int marginStart = typedArray.getDimensionPixelSize(R.styleable.SmartTableView_verticalSplitterWidth, -1);
                if (marginStart != -1) {
                    mContentMarginStart = marginStart;
                }
                int marginTop = typedArray.getDimensionPixelSize(R.styleable.SmartTableView_horizontalSplitterHeight, -1);
                if (marginTop != -1) {
                    mContentMarginTop = marginTop;
                }
                typedArray.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mHeaderLayoutManager = new SmartHeaderLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        mSidebarLayoutManager = new SmartVerticalLayoutManager(getContext(), RecyclerView.VERTICAL, false, this);
        mContentLayoutManager = new SmartVerticalLayoutManager(getContext(), RecyclerView.VERTICAL, false, this);

    }

    private boolean marginsSets = false;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!marginsSets && mShowDefaultDividers) {
            setContentStartMargin(mContentMarginStart);
            setContentTopMargin(mContentMarginTop);
            marginsSets = true;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setContentStartMargin(int marginStart) {
        mContentMarginStart = marginStart;
        ConstraintLayout.LayoutParams contentParams = (LayoutParams) mContentRecyclerView.getLayoutParams();
        contentParams.setMarginStart(marginStart);
        ConstraintLayout.LayoutParams headerParams = (LayoutParams) mHeaderRecyclerView.getLayoutParams();
        headerParams.setMarginStart(marginStart);
        mContentRecyclerView.setLayoutParams(contentParams);
        mHeaderRecyclerView.setLayoutParams(headerParams);
    }

    private void setContentTopMargin(int topMargin) {
        mContentMarginTop = topMargin;
        ConstraintLayout.LayoutParams contentParams = (LayoutParams) mContentRecyclerView.getLayoutParams();
        contentParams.topMargin = topMargin;
        ConstraintLayout.LayoutParams headerParams = (LayoutParams) mSidebarRecyclerView.getLayoutParams();
        headerParams.topMargin = topMargin;
//        mContentRecyclerView.setLayoutParams(params);

    }


    @SuppressWarnings("rawtypes")
    public void setAdapter(SmartBaseTableAdapter adapter) {
        mAdapter = adapter;
        clearAllHolders();
        mContentRecyclerView.setItemAnimator(null);
        mSidebarRecyclerView.setItemAnimator(null);
        mContentRecyclerView.setItemViewCacheSize(2);
        mSidebarRecyclerView.setItemViewCacheSize(2);
        if (mShowHeader) {
            mHeaderRecyclerView.setLayoutManager(mHeaderLayoutManager);

        }
        if (mShowSideBar) {
            mSidebarRecyclerView.setLayoutManager(mSidebarLayoutManager);

        }
        mContentRecyclerView.setLayoutManager(mContentLayoutManager);

        SmartSidebarAdapter sidebarAdapter = null;
        if (mShowSideBar) {
            sidebarAdapter = new SmartSidebarAdapter(adapter, this);
        }
        SmartHeaderAdapter headerAdapter = null;
        if (mShowHeader) {
            headerAdapter = new SmartHeaderAdapter(adapter, this);
        }
        SmartContentAdapter contentAdapter = new SmartContentAdapter(adapter, this);
        mVerticalScrollListener = new VerticalScrollListener(this);
        horizontalScrollListener = new HorizontalScrollListener(this);
        if (mShowHeader) {
            mHeaderRecyclerView.addOnItemTouchListener(horizontalScrollListener);
        }
        contentAdapter.setOnItemTouchListener(horizontalScrollListener);
        if (mShowSideBar) {
            mSidebarRecyclerView.addOnItemTouchListener(mVerticalScrollListener);
        }
        mContentRecyclerView.addOnItemTouchListener(mVerticalScrollListener);


        if (mShowSideBar) {
            mSidebarRecyclerView.setAdapter(sidebarAdapter);
        }
        if (mShowHeader) {
            mHeaderRecyclerView.setAdapter(headerAdapter);
        }
        mContentRecyclerView.setAdapter(contentAdapter);

        if (headerAdapter != null && mShowHeader) {
            headerAdapter.notifyDataSetChanged();
        }
        if (sidebarAdapter != null && mShowSideBar) {
            sidebarAdapter.notifyDataSetChanged();
        }
        mHeaderRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mHeaderRecyclerView.scrollToPosition(0);
            }
        });

        adapter.setNotifyObserver(this);

    }


    @SuppressWarnings("ConstantConditions")
    private int getMaxColumnWidth(int position) {

        SparseIntArray intArray = new SparseIntArray();
        int childrenCount = mContentRecyclerView.getChildCount();
        for (int i = 0; i < childrenCount; i++) {
            SmartHorizontalContentLayoutManager horizontalLayoutManager =
                    (SmartHorizontalContentLayoutManager) ((SmartRecyclerView) mContentRecyclerView.getChildAt(i)).getLayoutManager();
            intArray.put(i, horizontalLayoutManager.getCellsWidthHolder().get(position));
        }
        intArray.put(intArray.size(), getHeaderLayoutManager().getCellsWidthHolder().get(position));
        return SparseUtil.getMax(intArray);
    }

    public SmartRecyclerView getHeaderRecyclerView() {
        return mHeaderRecyclerView;
    }

    public SmartHeaderLayoutManager getHeaderLayoutManager() {
        return mHeaderLayoutManager;
    }

    public SmartRecyclerView getSidebarRecyclerView() {
        return mSidebarRecyclerView;
    }

    public SmartVerticalLayoutManager getSidebarLayoutManager() {
        return mSidebarLayoutManager;
    }

    public SmartRecyclerView getContentRecyclerView() {
        return mContentRecyclerView;
    }

    public LinearLayoutManager getContentLayoutManager() {
        return mContentLayoutManager;
    }

    public SmartBaseTableAdapter getAdapter() {
        return mAdapter;
    }

    public FrameLayout getCornerView() {
        return mCornerView;
    }

    public SparseIntArray getHorizontalWidthHolder() {
        return horizontalWidthHolder;
    }

    public HorizontalScrollListener getHorizontalScrollListener() {
        return horizontalScrollListener;
    }

    public VerticalScrollListener getVerticalScrollListener2() {
        return mVerticalScrollListener;
    }

    public boolean sideBarIsShowing() {
        return mShowSideBar;
    }

    public void setShowSideBar(boolean showSideBar) {
        mShowSideBar = showSideBar;
        Group sidebarGroup = findViewById(R.id.smart_table_view_sidebarGroup);
        if (mShowSideBar) {
            if (sidebarGroup.getVisibility() != VISIBLE) {
                sidebarGroup.setVisibility(View.VISIBLE);
            }
        } else {
            if (sidebarGroup.getVisibility() == VISIBLE) {
                sidebarGroup.setVisibility(View.GONE);
            }
        }
    }

    public boolean headerIsShowing() {
        return mShowHeader;
    }

    public void setShowHeader(boolean showHeader) {
        mShowHeader = showHeader;
        Group headerGroup = findViewById(R.id.smart_table_view_headerGroup);
        ConstraintLayout.LayoutParams contentRecyclerViewParams = (LayoutParams) mContentRecyclerView.getLayoutParams();

        if (mShowHeader) {

            contentRecyclerViewParams.width = 0;
            contentRecyclerViewParams.endToEnd = mHeaderRecyclerView.getId();
            headerGroup.setVisibility(View.VISIBLE);
            contentRecyclerViewParams.constrainedWidth = true;
        } else {
            contentRecyclerViewParams.width = LayoutParams.WRAP_CONTENT;
            contentRecyclerViewParams.endToEnd = 0;
            contentRecyclerViewParams.constrainedWidth = true;
            headerGroup.setVisibility(View.GONE);
        }
        mContentRecyclerView.setLayoutParams(contentRecyclerViewParams);
    }

    public boolean isStretchIfNotFit() {
        return mStretchIfNotFit;
    }

    public void setStretchIfNotFit(boolean stretchIfNotFit) {
        mStretchIfNotFit = stretchIfNotFit;
    }

    SparseIntArray getWidthIntArray() {
        return mWidthIntArray;
    }

    void setWidthIntArray(SparseIntArray widthIntArray) {
        mWidthIntArray = widthIntArray;
    }

    SparseIntArray calculateStretchWidth() {
        if (!mFitHorizontally) {
            mWidthIntArray = SmartTableUtil.getStretchedWidth(this);
        } else  {
            mWidthIntArray = calculateAverageWidth();
        }
        return mWidthIntArray;
    }

    SparseIntArray calculateAverageWidth() {
        mWidthIntArray = SmartTableUtil.getAverageWidth(this);
        return mWidthIntArray;
    }

    public boolean isFitHorizontally() {
        return mFitHorizontally;
    }

    public void setFitHorizontally(boolean fitHorizontally) {
        mFitHorizontally = fitHorizontally;
    }

    public void addColumnItemDecoration(RecyclerView.ItemDecoration headerItemDecoration,
                                        RecyclerView.ItemDecoration contentColumnItemDecoration) {
        mHeaderItemDecoration = headerItemDecoration;
        mHeaderRecyclerView.addItemDecoration(headerItemDecoration);
        mContentColumnItemDecoration = contentColumnItemDecoration;
    }

    public void addRowItemDecoration(RecyclerView.ItemDecoration sidebarItemDecoration,
                                     RecyclerView.ItemDecoration contentRowItemDecoration) {
        mSidebarItemDecoration = sidebarItemDecoration;
        mSidebarRecyclerView.addItemDecoration(mSidebarItemDecoration);
        mContentRowItemDecoration = contentRowItemDecoration;
        mContentRecyclerView.addItemDecoration(mContentRowItemDecoration);
    }

    public RecyclerView.ItemDecoration getContentColumnItemDecoration() {
        return mContentColumnItemDecoration;
    }

    public void setContentColumnItemDecoration(RecyclerView.ItemDecoration contentColumnItemDecoration) {
        mContentColumnItemDecoration = contentColumnItemDecoration;
    }

    public RecyclerView.ItemDecoration getHeaderItemDecoration() {
        return mHeaderItemDecoration;
    }

    public void setHeaderItemDecoration(RecyclerView.ItemDecoration headerItemDecoration) {
        mHeaderItemDecoration = headerItemDecoration;
    }

    public int getContentMarginStart() {
        return mContentMarginStart;
    }

    public int getContentMarginTop() {
        return mContentMarginTop;
    }


    public SparseIntArray getHeightSparseArray() {
        return mHeightSparseArray;
    }


    @Override
    public void dataSetChanged() {

        setAdapter(mAdapter);
    }

    private void clearAllHolders() {
        mWidthIntArray.clear();
        mHeightSparseArray.clear();

        if (headerIsShowing() && mHeaderRecyclerView.getAdapter() != null) {
            mHeaderLayoutManager.getCellsWidthHolder().clear();
        }
        if (sideBarIsShowing() && mSidebarRecyclerView.getAdapter() != null) {
            mSidebarLayoutManager.getCellsHeightHolder().clear();
        }

    }


    /**
     * if content fills its content earlier than sidebar then content adapter can not measuring sidebar height
     * in this case we set this with sidebar position to true , that means sidebar was null
     * when sidebar is binding must check this value , if true then notify content row to change its size
     */
    public SparseBooleanArray getSidebarNullLayoutManagerHolder() {
        return sidebarNullLayoutManagerHolder;
    }
}
