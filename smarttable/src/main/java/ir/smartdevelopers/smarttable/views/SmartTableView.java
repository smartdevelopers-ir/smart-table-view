package ir.smartdevelopers.smarttable.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.annotation.Px;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.os.TraceCompat;
import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ir.smartdevelopers.smarttable.R;
import ir.smartdevelopers.smarttable.views.TableView2.SmartBaseTableAdapter;
import ir.smartdevelopers.smarttable.views.listeners.Callback;
import ir.smartdevelopers.smarttable.views.listeners.OnContentItemClickListener;
import ir.smartdevelopers.smarttable.views.listeners.OnContentItemLongClickListener;
import ir.smartdevelopers.smarttable.views.listeners.OnHeaderItemClickListener;
import ir.smartdevelopers.smarttable.views.listeners.OnHeaderItemLongClickListener;
import ir.smartdevelopers.smarttable.views.listeners.OnSidebarItemClickListener;
import ir.smartdevelopers.smarttable.views.listeners.OnSidebarItemLongClickListener;

public class SmartTableView extends ConstraintLayout implements NotifyObserver {
    private NestedScrollView sidebarScrollView, contentVerticalScrollView;
    private SmartHorizontalScrollView headerScrollView, contentHorizontalScrollView;

//    private RelativeLayout root;
    private LinearLayout mSidebarTableLayout;
//    private TableLayout mContentTableLayout;
    private SmartTableLayout mContentTableLayout;
    private LinearLayout mHeaderTableRow;
    private Drawable mSidebarItemBackgroundDrawable, mHeaderItemBackgroundDrawable;
    private Drawable mContentItemBackgroundDrawable;
    /* corner top right view */
    private RelativeLayout cornerTopRightContainer;
    private int cornerTopRightResourceId;
    private View cornerTopRightView;

    private List<BaseSmartSidebarItemViewHolder> mSideBarItemList = new ArrayList<>();
    private List<BaseSmartHeaderItemViewHolder> mHeaderItemList = new ArrayList<>();
    private List<BaseSmartContentItemViewHolder> mContentItemList = new ArrayList<>();

    private List<Integer> maxColumnsWidthList;
    private List<Integer> maxRowHeightList;

    /* arrays of measure size*/
    private List<Pair<Integer, Integer>> mSidebarItemsSize;
    private List<Pair<Integer, Integer>> mHeaderItemsSize;
    private List<Pair<Integer, Integer>> mContentItemsSize;

    private BaseSmartTableAdapter mSmartTableAdapter;

    /* click listeners*/
    private OnSidebarItemClickListener mSidebarItemClickListener;
    private OnHeaderItemClickListener mHeaderItemClickListener;
    private OnContentItemClickListener mContentItemClickListener;
    /*long click listeners*/
    private OnSidebarItemLongClickListener mSidebarItemLongClickListener;
    private OnHeaderItemLongClickListener mHeaderItemLongClickListener;
    private OnContentItemLongClickListener mContentItemLongClickListener;

    /* for detect scrolling is by human or not*/
    private boolean sidebarIsScrolling, headerIsScrolling, contentIsScrolling = false;

    /* dividers */
    private Drawable mHorizontalContentDivider;
    private Drawable mVerticalContentDivider;
    private Drawable mHorizontalSidebarDivider;
    private Drawable mVerticalHeaderDivider;
    private int horizontalDividerHeight;
    private int verticalDividerWidth;
    /* table splitter*/
    private Drawable mHorizontalTableSplitter;
    private Drawable mVerticalTableSplitter;
    private int horizontalTableSplitterHeight;
    private int verticalTableSplitterWidth;
    private View mHorizontalTableSplitterView;
    private View mVerticalTableSplitterView;
    /* header background*/
    private Drawable mHeaderBackground;
    /* sidebar background*/
    private Drawable mSidebarBackground;
    /*content background */
    private Drawable mContentBackground;
    /* show and hide sidebar or header*/
    private boolean mShowSidebar = true;
    private boolean mShowHeader = true;
    /*for fit content to display horizontally*/
    private boolean mFitHorizontally=false;
    /*if true header items width matches to content width*/
    private boolean mStretchHeaderToContentWidth;
    /*if true content items width matches to header width*/
    private boolean mStretchContentToHeaderWidth;
    /*if true fit all width of headers or contents to max width*/
    private boolean mFitToMaxWidth=false;
    /*if content not fill screen stretch other content to fit screen*/
    private boolean mStretchIfContentNotFitScreen;
    private ExecutorService mExecutorService=Executors.newSingleThreadExecutor();


    public SmartTableView(Context context) {
        super(context);
        init(context, null);
    }

    public SmartTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }



    private void init(Context context, AttributeSet attrs) {

        inflate(context, R.layout.smart_table_table_layout, this);
        sidebarScrollView = findViewById(R.id.sideBar_holder);
        contentVerticalScrollView = findViewById(R.id.content_vertical_scrollView);
        headerScrollView = findViewById(R.id.header_holder);
        contentHorizontalScrollView = findViewById(R.id.content_horizontal_scrollView);
        cornerTopRightContainer = findViewById(R.id.table_right_top_corner);
        mSidebarTableLayout = findViewById(R.id.sidebar_tableLayout);
        mContentTableLayout = findViewById(R.id.content_tableLayout);
//        root = findViewById(R.id.table_root);
        mHeaderTableRow = findViewById(R.id.header_tableRow);
        mHorizontalTableSplitterView = findViewById(R.id.horizontal_line);
        mVerticalTableSplitterView = findViewById(R.id.vertical_line);
        horizontalDividerHeight = getResources().getDimensionPixelSize(R.dimen.smart_table_default_horizontal_divider_height);
        verticalDividerWidth = getResources().getDimensionPixelSize(R.dimen.smart_table_default_vertical_divider_width);

        manageScrollViews();

        if (attrs != null) {

            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartTableView);

            /* layout sizes*/
//            int layoutWidth = typedArray.getLayoutDimension(R.styleable.SmartTableView_android_layout_width,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            setLayoutWidth(layoutWidth);
//            int layoutHeight = typedArray.getLayoutDimension(R.styleable.SmartTableView_android_layout_height,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            setLayoutHeight(layoutHeight);

//            horizontalDividerHeight = typedArray.getDimensionPixelSize(R.styleable.SmartTableView_sidebarItemBottomMargin,
//                    getResources().getDimensionPixelSize(R.dimen.smart_table_default_horizontal_divider_height));
//            verticalDividerWidth = typedArray.getDimensionPixelSize(R.styleable.SmartTableView_headerBetweenItemMargin,
//                    getResources().getDimensionPixelSize(R.dimen.smart_table_default_vertical_divider_width));

            /* get sidebar item background from xml attribute*/
            mSidebarItemBackgroundDrawable = typedArray.getDrawable(R.styleable.SmartTableView_sidebarItemBackground);

            /* get header item background from xml attribute*/
            mHeaderItemBackgroundDrawable = typedArray.getDrawable(R.styleable.SmartTableView_headerItemBackground);

            /* get content item background from xml attribute*/
            mContentItemBackgroundDrawable = typedArray.getDrawable(R.styleable.SmartTableView_contentItemBackground);

            Drawable cornerTopDrawable = typedArray.getDrawable(R.styleable.SmartTableView_cornerTopRightColor);
            if (cornerTopDrawable != null) {
                setCornerTopBackgroundDrawable(cornerTopDrawable);
            }

            cornerTopRightResourceId = typedArray.getResourceId(R.styleable.SmartTableView_cornerTopRightView, 0);
            if (cornerTopRightResourceId!=0) {
                setCornerTopView(cornerTopRightResourceId);
            }
            /* backgrounds */
            mHeaderBackground = typedArray.getDrawable(R.styleable.SmartTableView_headerBackground);
            if (mHeaderBackground != null)
                setHeaderBackground(mHeaderBackground);
            mSidebarBackground = typedArray.getDrawable(R.styleable.SmartTableView_sidebarBackground);
            if (mSidebarBackground != null)
                setSidebarBackground(mSidebarBackground);
            mContentBackground = typedArray.getDrawable(R.styleable.SmartTableView_contentBackground);
            if (mContentBackground != null)
                setContentBackground(mContentBackground);

            /* show or hide sidebar or header */
            boolean showSidebar = typedArray.getBoolean(R.styleable.SmartTableView_showSidebar, true);
            setShowSidebar(showSidebar);
            boolean showHeader = typedArray.getBoolean(R.styleable.SmartTableView_showHeader, true);
            setShowHeader(showHeader);

            /*fit horizontally*/
            mFitHorizontally=typedArray.getBoolean(R.styleable.SmartTableView_fitHorizontally,false);
            setFitHorizontally(mFitHorizontally);
            /*stretch content width to header width */
            mStretchContentToHeaderWidth=typedArray.getBoolean(R.styleable.SmartTableView_stretchContentWidthToHeaderWidth,false);
            setStretchContentToHeaderWidth(mStretchContentToHeaderWidth);
            /*stretch header width to content width */
            mStretchHeaderToContentWidth=typedArray.getBoolean(R.styleable.SmartTableView_stretchHeaderWidthToContentWidth,false);
            setStretchHeaderToContentWidth(mStretchHeaderToContentWidth);
            /*fit all content width to max of columns*/
            mFitToMaxWidth=typedArray.getBoolean(R.styleable.SmartTableView_fitAllWidthToMaxOfColumnWidth,false);
            setFitToMaxWidth(mFitToMaxWidth);

            mStretchIfContentNotFitScreen=typedArray.getBoolean(R.styleable.SmartTableView_stretchIfContentNotFitScreen,false);
            setStretchIfContentNotFitScreen(mStretchIfContentNotFitScreen);

            mHorizontalContentDivider=typedArray.getDrawable(R.styleable.SmartTableView_contentHorizontalDivider);
            mVerticalContentDivider=typedArray.getDrawable(R.styleable.SmartTableView_contentVerticalDivider);
            mVerticalHeaderDivider=typedArray.getDrawable(R.styleable.SmartTableView_headerDivider);
            mHorizontalSidebarDivider=typedArray.getDrawable(R.styleable.SmartTableView_sidebarDivider);

            boolean showDefaultDividers=typedArray.getBoolean(R.styleable.SmartTableView_showDefaultDividers,true);
            if (showDefaultDividers){
                Drawable divider= ContextCompat.getDrawable(context,R.drawable.horizontal_divider_default);
                if (mHorizontalContentDivider==null){
                    mHorizontalContentDivider=divider;
                }
                if (mVerticalContentDivider==null){
                    mVerticalContentDivider=divider;
                }
                if (mVerticalHeaderDivider==null){
                    mVerticalHeaderDivider=divider;
                }
                if (mHorizontalSidebarDivider==null){
                    mHorizontalSidebarDivider=divider;
                }
            }
            if (mHorizontalContentDivider != null) {
                horizontalDividerHeight = mHorizontalContentDivider.getIntrinsicHeight();
            }
            if (mVerticalContentDivider != null) {
                verticalDividerWidth = mVerticalContentDivider.getIntrinsicWidth();
            }
            typedArray.recycle();
        }

    }


    //    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        if (cornerTopRightResourceId != 0) {
//            setCornerTopView(cornerTopRightResourceId);
//        }
//    }

    @SuppressLint("ClickableViewAccessibility")
    private void manageScrollViews() {

        sidebarScrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mShowSidebar) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_SCROLL ||
                            motionEvent.getAction() == MotionEvent.ACTION_MOVE ||
                            motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        sidebarIsScrolling = true;
                    } else {
                        sidebarIsScrolling = false;
                    }
                }

                return false;
            }
        });
        contentHorizontalScrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_SCROLL ||
                        motionEvent.getAction() == MotionEvent.ACTION_MOVE ||
                        motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    contentIsScrolling = true;
                } else {
                    contentIsScrolling = false;
                }

                return false;
            }
        });
        contentVerticalScrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_SCROLL ||
                        motionEvent.getAction() == MotionEvent.ACTION_MOVE ||
                        motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    contentIsScrolling = true;
                } else {
                    contentIsScrolling = false;
                }


                return false;
            }
        });
        headerScrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mShowHeader) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_SCROLL ||
                            motionEvent.getAction() == MotionEvent.ACTION_MOVE ||
                            motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        headerIsScrolling = true;
                    } else {
                        headerIsScrolling = false;
                    }
                }
                return false;
            }
        });


        contentHorizontalScrollView.setOnScrollChangeListener(new ir.smartdevelopers.smarttable.views.listeners.OnScrollChangeListener() {
            @Override
            public void onScrollChanged(SmartHorizontalScrollView smartHorizontalScrollView, int x, int y, int oldX, int oldY) {
                /* scroll when header is scrolling by user
                 * when is not scrolling automatically
                 * and if header is showing
                 */

                if (mShowHeader && !headerIsScrolling) {
                    headerScrollView.scrollTo(x, y);
                }
            }
        });
        headerScrollView.setOnScrollChangeListener(new ir.smartdevelopers.smarttable.views.listeners.OnScrollChangeListener() {
            @Override
            public void onScrollChanged(SmartHorizontalScrollView smartHorizontalScrollView, int x, int y, int oldX, int oldY) {
                /* scroll when content is scrolling horizontally by user
                 * when is not scrolling automatically
                 */
                if (mShowHeader && !contentIsScrolling) {
                    contentHorizontalScrollView.scrollTo(x, y);
                }
            }
        });

        sidebarScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                /* scroll when content is scrolling vertically by user
                 * when is not scrolling automatically
                 */
                if (mShowSidebar && !contentIsScrolling) {
                    contentVerticalScrollView.scrollTo(scrollX, scrollY);
                }
            }
        });
        contentVerticalScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                /* scroll when sidebar is scrolling  by user
                 * when is not scrolling automatically
                 */
                if (mShowSidebar && !sidebarIsScrolling) {
                    sidebarScrollView.scrollTo(scrollX, scrollY);
                }
            }
        });
    }


    /*
     * add all of SmartSideBarItem to the table of sidebar
     * and after all of items was added , it sets all of items width
     * from table width .and fill mSidebarItemsHeight with height of all
     * items to use later for setting content size
     * */
    private void setSidebarItems(BaseSmartTableAdapter adapter) {


        int size = adapter.getRowCount();
        mSidebarTableLayout.removeAllViewsInLayout();
        /* set horizontal divider */
        if (mHorizontalSidebarDivider!=null){
            mSidebarTableLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE );
            mSidebarTableLayout.setDividerDrawable(mHorizontalSidebarDivider);
        }
        /* get max size */
        int maxWidth = getMaxWidth(mSidebarItemsSize);
        mSidebarTableLayout.getLayoutParams().width=maxWidth;
        for (int row = 0; row < size; row++) {
            final BaseSmartSidebarItemViewHolder sidebarViewHolder = mSideBarItemList.get(row);
//            FrameLayout tableRow = (FrameLayout) sidebarViewHolder.getParent();

            /* get max size */
            int maxHeight = maxRowHeightList.get(row);
            /* set size for sidebar items before rendering*/
//            sidebarViewHolder.setLayoutWidth(maxWidth);
//            sidebarViewHolder.setLayoutHeight(maxHeight);
            View sidebarView = sidebarViewHolder.getItemView();
            ViewGroup.LayoutParams params=sidebarView.getLayoutParams();
            params.width=maxWidth;
            params.height=maxHeight;
//            sidebarView.requestLayout();

//            tableRow.removeViewInLayout(sidebarView);
            /* add click listener*/
            final int sidebarPosFinal = row;
            if (mSidebarItemClickListener != null) {
                sidebarView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSidebarItemClickListener.onSidebarItemClicked(sidebarViewHolder, sidebarPosFinal);
                    }
                });
            }
            /* add long click listener*/
            if (mSidebarItemLongClickListener != null) {
                sidebarView.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return mSidebarItemLongClickListener.onSidebarItemClicked(sidebarViewHolder, sidebarPosFinal);
                    }
                });
            }
            /* setBackground */
            if (mSidebarItemBackgroundDrawable != null) {
                sidebarViewHolder.setBackground(mSidebarItemBackgroundDrawable);
            }
            /* set row background */
            if (adapter.getSidebarRowBackground(sidebarPosFinal) != null) {
                sidebarView.setBackground(adapter.getSidebarRowBackground(sidebarPosFinal));
            }

            if (sidebarView.getParent()!=null){
                ((ViewGroup)sidebarView.getParent()).removeView(sidebarView);
            }

//            tableRow.addView(sidebarView);
//           if (tableRow.getParent()!=null){
//                ((ViewGroup)tableRow.getParent()).removeView(tableRow);
//            }
//            mSidebarTableLayout.addView(tableRow);
            mSidebarTableLayout.addView(sidebarView);
            /* set horizontal divider */
//            if (horizontalDividerHeight != 0) {
//                if (row < size - 1) {
//                    View horizontalDivider = new View(getContext());
//                    horizontalDivider.setLayoutParams(new TableRow.
//                            LayoutParams(maxWidth, horizontalDividerHeight
//                    ));
//                    if (mHorizontalSidebarDivider != null) {
//                        horizontalDivider.setBackground(mHorizontalSidebarDivider);
//                    }
//                    mSidebarTableLayout.addView(horizontalDivider);
//                }
//            }


        }
    }


    /*
     * add all of SmartHeaderItem to the header of table
     * * if content size is bigger than header column,
     * header item width must set to max content width column
     * */
    private void setHeaderItems(BaseSmartTableAdapter adapter) {


        /* first generate header items then generate content item
         *to calculate columns width
         */


        int size = adapter.getColumnCount();
        /* remove all childes to add dividers between childes*/
        mHeaderTableRow.removeAllViews();

        int maxWidthOfColumns=0;
        if (mFitToMaxWidth){
            if (maxColumnsWidthList!=null && maxColumnsWidthList.size()>0)
             maxWidthOfColumns=Collections.max(maxColumnsWidthList);
        }
        int stretchedWidth=0;
        if (mStretchIfContentNotFitScreen) {
             stretchedWidth = calculateWidthToFitScreen();
        }
        int averageWidth=0;
        if (mFitHorizontally){
             averageWidth=getContentAverageWidth(size);
        }
        /* header divider */
        if (mVerticalHeaderDivider!=null){
            mHeaderTableRow.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_BEGINNING);
            mHeaderTableRow.setDividerDrawable(mVerticalHeaderDivider);
        }
        /* get max header items height*/
        int maxHeight = getMaxHeight(mHeaderItemsSize);
        mHeaderTableRow.getLayoutParams().height=maxHeight;
        for (int col = 0; col < size; col++) {
            final BaseSmartHeaderItemViewHolder headerViewHolder = mHeaderItemList.get(col);


            View headerView=headerViewHolder.getItemView();
            /* set max height to all header items*/
//            headerViewHolder.setLayoutHeight(maxHeight);
            ViewGroup.LayoutParams headerParams=headerView.getLayoutParams();
            if (headerParams==null){
                headerParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,maxHeight);
            }
            headerParams.height=maxHeight;

            /*if fit horizontally is true set average of width to the header*/
            if(mFitHorizontally){

//                headerViewHolder.setLayoutWidth(averageWidth);
                headerParams.width=averageWidth;
            }else {
                if (mFitToMaxWidth){
                    /*set max of columns width to the all header*/
//                    headerViewHolder.setLayoutWidth(maxWidthOfColumns);
                    headerParams.width=maxWidthOfColumns;
                }else if(mStretchIfContentNotFitScreen && stretchedWidth!=-2){

                    if (!mStretchToFitDeletedPosition.contains(col)){
//                        headerViewHolder.setLayoutWidth(stretchedWidth);
                        headerParams.width=stretchedWidth;
                    }else {
//                        headerViewHolder.setLayoutWidth(maxColumnsWidthList.get(col));
                        headerParams.width=maxColumnsWidthList.get(col);
                    }
                }

                else {
                    /* set max width of current column to headerViewHolder*/
//                    headerViewHolder.setLayoutWidth(maxColumnsWidthList.get(col));
                    headerParams.width=maxColumnsWidthList.get(col);
                }
            }

//            headerView.requestLayout();


            /* add click listener*/
            final int headerFinalPos = col;
            if (mHeaderItemClickListener != null) {
                headerView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mHeaderItemClickListener.onHeaderItemClicked(headerViewHolder, headerFinalPos);
                    }
                });
            }
            /* add long click listener*/
            if (mHeaderItemLongClickListener != null) {
                headerView.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return mHeaderItemLongClickListener.onHeaderItemClicked(headerViewHolder, headerFinalPos);
                    }
                });
            }
            /*set background*/
            if (mHeaderItemBackgroundDrawable != null) {
                headerViewHolder.setBackground(mHeaderItemBackgroundDrawable);
            }

            if (headerView.getParent()!=null){
                ((ViewGroup)headerView.getParent()).removeView(headerView);
            }
            mHeaderTableRow.addView(headerView);


//            if (verticalDividerWidth != 0) {
//                if (col < size - 1) {
//                    View verticalDivider = new View(getContext());
//                    verticalDivider.setLayoutParams(new TableRow.
//                            LayoutParams(verticalDividerWidth, maxHeight));
//                    if (mVerticalHeaderDivider != null) {
//                        verticalDivider.setBackground(mVerticalHeaderDivider);
//                    }
//                    mHeaderTableRow.addView(verticalDivider);
//                }
//            }

        }
    }

    private int getContentAverageWidth(int count) {

        if (count==0){return 0;}
        int deviceWidth=getResources().getDisplayMetrics().widthPixels;
        int sidebarWidth=0;
        if (mShowSidebar){
            sidebarWidth=getMaxWidth(mSidebarItemsSize);
        }
        return ((deviceWidth-sidebarWidth)-(count*verticalDividerWidth))/count;

    }

    /*
     * this method called after setHeader and fill content
     * the content must be the same size of its header column and its sidebar row
     * for each sidebar item one tableRow
     * for each header item one content
     * */
    @SuppressWarnings("rawtypes")
    private void setContents(BaseSmartTableAdapter adapter) {


        final int numOfSidebarItem = adapter.getRowCount();
        final int numOfHeaderItem = adapter.getColumnCount();
        mContentTableLayout.removeAllViewsInLayout();

        int index = 0;
        int maxWidthOfColumns=0;
        if (mFitToMaxWidth) {
            if (maxColumnsWidthList!=null && maxColumnsWidthList.size()>0)
            maxWidthOfColumns=Collections.max(maxColumnsWidthList);
        }
        int stretchedWidth=0;
        if (mStretchIfContentNotFitScreen){
            stretchedWidth=calculateWidthToFitScreen();
        }
        int averageWidth=0;
        if (mFitHorizontally){
             averageWidth=getContentAverageWidth(numOfHeaderItem);
        }

//        if (horizontalDividerHeight!=0){
//            mContentTableLayout.setDividerPadding(horizontalDividerHeight);
            if (mHorizontalContentDivider!=null){
                mContentTableLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE );
                mContentTableLayout.setDividerDrawable(mHorizontalContentDivider);
            }
//        }
        for (int row = 0; row < numOfSidebarItem; row++) {
            TableRow tableRow = (TableRow) mContentItemList.get(index)
                    .getParent();

            if (adapter.getContentRowBackground(row) != null) {
                tableRow.setBackground(adapter.getContentRowBackground(row));
            }
            tableRow.removeAllViewsInLayout();
//            if (verticalDividerWidth!=0){
//                tableRow.setDividerPadding(verticalDividerWidth);
                if (mVerticalContentDivider!=null){
                    tableRow.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE|LinearLayout.SHOW_DIVIDER_BEGINNING);
                    tableRow.setDividerDrawable(mVerticalContentDivider);
                }
//            }

            /*remove all chide from tableRow to add dividers between childes */

            int maxHeight = maxRowHeightList.get(row);
            tableRow.getLayoutParams().height=maxHeight;
            for (int col = 0; col < numOfHeaderItem; col++) {

                final int rowPos=row;
                final int colPos=col;
                final BaseSmartContentItemViewHolder contentViewHolder = mContentItemList.get(index);
                /* get max width of column*/
                int maxWidth = maxColumnsWidthList.get(col);

                View contentView = contentViewHolder.getItemView();
                /* set max size  to contents*/
//                contentViewHolder.setLayoutHeight(maxHeight);
                ViewGroup.LayoutParams contentItemParams=contentView.getLayoutParams();
                contentItemParams.height=maxHeight;

                /*if fit horizontally is true set average width to content*/
                if (mFitHorizontally){

//                    contentViewHolder.setLayoutWidth(averageWidth);
                    contentItemParams.width=averageWidth;
                }else {
                    if (mFitToMaxWidth){
                        /*set max of columns width to the all contents*/
//                        contentViewHolder.setLayoutWidth(maxWidthOfColumns);
                        contentItemParams.width=maxWidthOfColumns;

                    }else if(mStretchIfContentNotFitScreen && stretchedWidth!=-2){

                        if (!mStretchToFitDeletedPosition.contains(col)){
//                            contentViewHolder.setLayoutWidth(stretchedWidth);
                            contentItemParams.width=stretchedWidth;

                        }else {
//                            contentViewHolder.setLayoutWidth(maxWidth);
                            contentItemParams.width=maxWidth;

                        }
                    }

                    else {
                        /*if fit horizontally is false set max width to content*/
//                        contentViewHolder.setLayoutWidth(maxWidth);
                        contentItemParams.width=maxWidth;

                    }
                }




                /* set click listener for contentViewHolder*/

                if (mContentItemClickListener != null) {
                    contentView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mContentItemClickListener.onContentItemClicked(contentViewHolder, rowPos, colPos);
                        }
                    });
                }
                /* set long click listener for contentViewHolder*/
                if (mContentItemLongClickListener != null) {
                    contentView.setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            return mContentItemLongClickListener.onContentItemClicked(contentViewHolder, rowPos, colPos);
                        }
                    });
                }
                /* set background if was set*/
                if (mContentItemBackgroundDrawable != null) {
                    contentViewHolder.setBackground(mContentItemBackgroundDrawable);
                }

                if (contentView.getParent()!=null){
                    ((ViewGroup)contentView.getParent()).removeView(contentView);
                }
                /* add contentViewHolder to table row*/
                tableRow.addView(contentView);
//                if (verticalDividerWidth != 0) {
//                    if (col < numOfHeaderItem - 1) {
//
//                        View verticalDivider = new View(getContext());
//                        verticalDivider.setLayoutParams(new TableRow.
//                                LayoutParams(verticalDividerWidth, maxHeight));
//                        if (mVerticalContentDivider != null) {
//                            verticalDivider.setBackground(mVerticalContentDivider);
//                        }
//
//                        tableRow.addView(verticalDivider);
//                    }
//                }
                index++;
            }



            mContentTableLayout.addView(tableRow);

            /* add divider */
//            if (horizontalDividerHeight != 0) {
//                View horizontalDivider = new View(getContext());
//
//                horizontalDivider.setLayoutParams(new TableLayout.
//                        LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, horizontalDividerHeight
//                ));
//                if (mHorizontalContentDivider != null) {
//                    horizontalDivider.setBackground(mHorizontalContentDivider);
//                }
//                mContentTableLayout.addView(horizontalDivider);
//            }
        }


    }


    @SuppressWarnings("unchecked")
    private List<BaseSmartSidebarItemViewHolder> generateSidebarItems(BaseSmartTableAdapter adapter) {
        int rowCount = adapter.getRowCount();
        /* create side bar measure size array*/
        mSidebarItemsSize = new ArrayList<>(rowCount);
        List<BaseSmartSidebarItemViewHolder> smartSidebarItemList = new ArrayList<>(rowCount);

        mSidebarTableLayout.getLayoutParams().height=ViewGroup.LayoutParams.WRAP_CONTENT;
        mSidebarTableLayout.getLayoutParams().width=ViewGroup.LayoutParams.WRAP_CONTENT;
        /* get all items and measure view size*/
        for (int i = 0; i < rowCount; i++) {
//            FrameLayout tableRow;
//            if (mSideBarItemList.size() > i) {
//                ViewParent parent = mSideBarItemList.get(i).getParent();
//                tableRow = (FrameLayout) parent;
//            } else {
//                tableRow = new FrameLayout(getContext());
//
//            }
//            tableRow.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
//                    FrameLayout.LayoutParams.WRAP_CONTENT));
            /*
             *generate BaseSmartSidebarItem from adapter by calling
             *adapter.onCreateSideBarItem
             */
            final BaseSmartSidebarItemViewHolder sidebarViewHolder = getSidebarItemViewHolder(mSidebarTableLayout,
                    adapter, i);
            /*set tableRow as sidebarViewHolder parent to fetch it later */
            sidebarViewHolder.setParent(mSidebarTableLayout);
            /* call onBind after view holder created */
            adapter.onBindSidebarViewHolder(sidebarViewHolder, i);

            View sidebarView = sidebarViewHolder.getItemView();

            /* measure size*/
//            mSidebarItemsSize.add(measureSize(sidebarView));

            /* add BaseSmartSidebarItem to mSideBarItemList*/
            smartSidebarItemList.add(sidebarViewHolder);
        }
        return smartSidebarItemList;
    }
//    private void generateSidebarItems(BaseSmartTableAdapter adapter, Callback<>)

    /*
     * generate HeaderItemsList and their size array
     * */
    @SuppressWarnings("unchecked")
    private List<BaseSmartHeaderItemViewHolder> generateHeaderItems(BaseSmartTableAdapter adapter) {
        int size = adapter.getColumnCount();
        mHeaderItemsSize = new ArrayList<>(size);

        List<BaseSmartHeaderItemViewHolder> headerItemsList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            /*
             *generate BaseSmartHeaderItem from adapter by calling
             *adapter.onCreateHeaderItem
             */


            final BaseSmartHeaderItemViewHolder headerViewHolder = getHeaderItemViewHolder(mHeaderTableRow,
                    adapter, i);
            /*set mHeaderTableRow as headerViewHolder parent to fetch it later */
            headerViewHolder.setParent(mHeaderTableRow);
            /* call onBind after create view holder */
            adapter.onBindHeaderViewHolder(headerViewHolder, i);
            /* get view from hodler to measure size*/
            View headerView = headerViewHolder.getItemView();

            /* measure size*/
//            mHeaderItemsSize.add(measureSize(headerView));

            /* add BaseSmartHeaderItem to mHeaderItemList*/
            headerItemsList.add(headerViewHolder);
        }
        return headerItemsList;
    }

    /*
     * generate ContentItemsList and their size array
     * */
    @SuppressWarnings("unchecked")
    private List<BaseSmartContentItemViewHolder> generateContentItems(BaseSmartTableAdapter adapter) {
        final int numOfSidebarItem = adapter.getRowCount();
        final int numOfHeaderItem = adapter.getColumnCount();
        final int numOfContentItem = adapter.getContentItemCount();

        mContentItemsSize = new ArrayList<>(numOfContentItem);
        List<BaseSmartContentItemViewHolder> smartContentItemList = new ArrayList<>();
        int index = 0;
        /* create content from mContentItemList*/
        for (int row = 0; row < numOfSidebarItem; row++) {
            TableRow tableRow;
            if (mContentItemList.size() > index) {
                ViewParent parent = mContentItemList.get(index).getParent();
                tableRow = (TableRow) parent;
            } else {
                tableRow = new TableRow(getContext());

            }
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            for (int col = 0; col < numOfHeaderItem; col++) {
                /*
                 *generate BaseSmartHeaderItem from adapter by calling
                 *adapter.onCreateHeaderItem
                 */
                final BaseSmartContentItemViewHolder contentViewHolder =
                        getContentItemViewHolder(tableRow, adapter, index, row, col);
                /*set tableRow as contentViewHolder parent to fetch it later */
                contentViewHolder.setParent(tableRow);
                /* after generate holder call onBind from adapter */
                adapter.onBindContentViewHolder(contentViewHolder, row, col);

                View contentView = contentViewHolder.getItemView();

                /* measure content width and add to list*/
//                mContentItemsSize.add(measureSize(contentView));

                /* add BaseSmartContentItem to mContentItemList*/
                smartContentItemList.add(contentViewHolder);

                index++;
            }

        }
        return smartContentItemList;
    }

    private BaseSmartSidebarItemViewHolder getSidebarItemViewHolder(ViewGroup parent,
                                                                    BaseSmartTableAdapter adapter,
                                                                    int position) {
        BaseSmartSidebarItemViewHolder oldSidebarViewHolder;
        try {
            oldSidebarViewHolder = mSideBarItemList.get(position);
        } catch (Exception e) {
            oldSidebarViewHolder = adapter.
                    onCreateSidebarViewHolder(parent);
            if (oldSidebarViewHolder!=null) {
                oldSidebarViewHolder.setDefaultLayoutParams(oldSidebarViewHolder.getItemView().getLayoutParams());
            }
        }
        if (oldSidebarViewHolder==null){
            throw new NullPointerException("When showSidebar set true SidebarItemViewHolder can not be null");
        }
        oldSidebarViewHolder.setAdapterPosition(position);
        oldSidebarViewHolder.getItemView().getLayoutParams().width=oldSidebarViewHolder.getDefaultLayoutParams().width;
        oldSidebarViewHolder.getItemView().getLayoutParams().height=oldSidebarViewHolder.getDefaultLayoutParams().height;
        return oldSidebarViewHolder;
    }

    private BaseSmartHeaderItemViewHolder getHeaderItemViewHolder(ViewGroup parent,
                                                                  BaseSmartTableAdapter adapter,
                                                                  int position) {
        BaseSmartHeaderItemViewHolder oldHeaderViewHolder=null;
        try {
            oldHeaderViewHolder = mHeaderItemList.get(position);
        } catch (Exception e) {
            oldHeaderViewHolder = adapter.
                    onCreateHeaderViewHolder(parent);
            if (oldHeaderViewHolder!=null){
                oldHeaderViewHolder.setDefaultLayoutParams(oldHeaderViewHolder.getItemView().getLayoutParams());
            }
        }
        if (oldHeaderViewHolder==null){
            throw new NullPointerException("When showHeader set true HeaderItemViewHolder can not be null");
        }
        oldHeaderViewHolder.setAdapterPosition(position);
        oldHeaderViewHolder.getItemView().getLayoutParams().width=oldHeaderViewHolder.getDefaultLayoutParams().width;
        oldHeaderViewHolder.getItemView().getLayoutParams().height=oldHeaderViewHolder.getDefaultLayoutParams().height;

        return oldHeaderViewHolder;
    }

    private BaseSmartContentItemViewHolder getContentItemViewHolder(ViewGroup parent,
                                                                    BaseSmartTableAdapter adapter,
                                                                    int index, int rowPosition, int colPosition) {
        BaseSmartContentItemViewHolder oldContentViewHolder;
        try {
            oldContentViewHolder = mContentItemList.get(index);
        } catch (Exception e) {
            oldContentViewHolder = adapter.
                    onCreateContentViewHolder(parent);
            oldContentViewHolder.setDefaultLayoutParams(oldContentViewHolder.getItemView().getLayoutParams());
        }
        oldContentViewHolder.setAdapterPosition(index);
        oldContentViewHolder.setContentPosition(rowPosition, colPosition);
        oldContentViewHolder.getItemView().getLayoutParams().width=oldContentViewHolder.getDefaultLayoutParams().width;
        oldContentViewHolder.getItemView().getLayoutParams().height=oldContentViewHolder.getDefaultLayoutParams().height;
        return oldContentViewHolder;
    }


    /*
     * generate MaxColumnWidth array
     * */
    private List<Integer> generateMaxColumnWidth(final int columnCount, List<Pair<Integer, Integer>> headerItemsSizeList,
                                                 List<Pair<Integer, Integer>> contentItemsSizeList) {
        List<Integer> result = new ArrayList<>(columnCount);
        if (columnCount == 0) {
            return result;
        }



        /* if contents is empty just return header width
        * or
        *if should stretch to header width return header width
        */
        if (contentItemsSizeList == null || contentItemsSizeList.isEmpty() || mStretchContentToHeaderWidth) {
            for (int i = 0; i < headerItemsSizeList.size(); i++) {
                int headerWidth = headerItemsSizeList.get(i).first;
                result.add(headerWidth);
            }
            return result;
        }
        /* if contents is not empty */

        List<List<Pair<Integer, Integer>>> cols = new ArrayList<>();
        /* fill empty list*/
        for (int i = 0; i < columnCount; i++) {
            cols.add(new ArrayList<Pair<Integer, Integer>>());
        }


        Comparator<Pair<Integer, Integer>> widthComparator = new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> item1, Pair<Integer, Integer> item2) {
                return item1.first - item2.first;
            }
        };
        /* put contentItemsSizeList in column array */
        for (int i = 0; i < contentItemsSizeList.size(); i++) {
            int colPos = i % columnCount;
            cols.get(colPos).add(contentItemsSizeList.get(i));
        }
        /* if header is empty just return contents width
        * or
        * if should stretch to content width return content width
        */
        if (headerItemsSizeList == null || headerItemsSizeList.isEmpty() || mStretchHeaderToContentWidth) {
            for (int i = 0; i < cols.size(); i++) {
                int maxFromColsWidth = (Collections.max(cols.get(i), widthComparator)).first;
                result.add(maxFromColsWidth);
            }
            return result;
        }

        /* else if both has data return max of them*/
        for (int i = 0; i < cols.size(); i++) {
            int maxFromColsWidth = (Collections.max(cols.get(i), widthComparator)).first;
            int headerWidth = headerItemsSizeList.get(i).first;
            result.add(Math.max(maxFromColsWidth, headerWidth));
        }
        return result;

    }


    private List<Integer> generateMaxRowHeight(final int rowCount,
                                               List<Pair<Integer, Integer>> sidebarItemsSizeList,
                                               List<Pair<Integer, Integer>> contentItemsSizeList) {


        List<Integer> result = new ArrayList<>(rowCount);
        if (rowCount == 0) {
            return result;
        }



        /* if contents is empty just return sidebars height*/
        if (contentItemsSizeList == null || contentItemsSizeList.size() == 0) {
            for (int row = 0; row < sidebarItemsSizeList.size(); row++) {
                int sidebarHeight = sidebarItemsSizeList.get(row).second;
                result.add(sidebarHeight);
            }
            return result;
        }
        /* if contents is not empty */
        List<List<Pair<Integer, Integer>>> rows = new ArrayList<>();

        /* fill empty list*/
        for (int i = 0; i < rowCount; i++) {
            rows.add(new ArrayList<Pair<Integer, Integer>>());
        }
        Comparator<Pair<Integer, Integer>> heightComparator = new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> item1, Pair<Integer, Integer> item2) {
                return item1.second - item2.second;
            }
        };

        /* put contentItemsSizeList in row array */

        int colCount = contentItemsSizeList.size() / rowCount;
        for (int i = 0; i < contentItemsSizeList.size(); i++) {
            int rowPos = i / colCount;
            rows.get(rowPos).add(contentItemsSizeList.get(i));
        }

        /* if sidebar is empty just return contents height*/
        if (sidebarItemsSizeList == null || sidebarItemsSizeList.size() == 0) {


            for (int i = 0; i < rows.size(); i++) {
                int maxFromRowHeight = (Collections.max(rows.get(i), heightComparator)).second;
                result.add(maxFromRowHeight);
            }
            return result;
        }

        /* else if both has data return max of them*/

        for (int i = 0; i < rows.size(); i++) {
            int maxFromRowHeight = (Collections.max(rows.get(i), heightComparator)).second;
            int sidebarHeight = sidebarItemsSizeList.get(i).second;
            result.add(Math.max(maxFromRowHeight, sidebarHeight));
        }

        return result;
    }

    private void updateTable(final BaseSmartTableAdapter adapter) {
        mSideBarItemList = Collections.emptyList();
        mHeaderItemList = Collections.emptyList();
        mContentItemList = Collections.emptyList();
        if (mShowSidebar && adapter.getRowCount()>0) {
//            if (adapter.getRowCount() == 1) {
                showSidebarView(true);
//            }
            TraceCompat.beginSection("generateSidebarItems");
            mSideBarItemList = generateSidebarItems(adapter);
            TraceCompat.endSection();

        } else {
//            mSideBarItemList = Collections.emptyList();
            mSidebarItemsSize = Collections.emptyList();
            showSidebarView(false);
        }
        if (mShowHeader && adapter.getColumnCount() > 0) {
//            if (adapter.getColumnCount() == 1) {
                showHeaderView(true);
//            }
            TraceCompat.beginSection("generateHeaderItems");
            mHeaderItemList = generateHeaderItems(adapter);
            TraceCompat.endSection();
        } else {
//            mHeaderItemList = Collections.emptyList();
            mHeaderItemsSize = Collections.emptyList();
            showHeaderView(false);
        }
        if (adapter.getContentItemCount() > 0) {
            if (!contentIsShowing()) {
                showContentView(true);
            }
            TraceCompat.beginSection("generateContentItems");
            mContentItemList = generateContentItems(adapter);
            TraceCompat.endSection();
            

        } else {
//            mContentItemList = Collections.emptyList();
            mContentItemsSize = Collections.emptyList();
            showContentView(false);
        }


        measureAsync(new Callback<Void>() {
            @Override
            public void onComplete(Void aVoid) {
                maxRowHeightList = generateMaxRowHeight(adapter.getRowCount(), mSidebarItemsSize, mContentItemsSize);
                maxColumnsWidthList = generateMaxColumnWidth(adapter.getColumnCount(), mHeaderItemsSize, mContentItemsSize);
                if (mShowSidebar && adapter.getRowCount() > 0) {
                    TraceCompat.beginSection("setSidebarItems");
                    setSidebarItems(mSmartTableAdapter);
                    TraceCompat.endSection();
                }
                if (mShowHeader && adapter.getColumnCount() > 0) {
                    TraceCompat.beginSection("setHeaderItems");
                    setHeaderItems(mSmartTableAdapter);
                    TraceCompat.endSection();
                }
                if (adapter.getContentItemCount() > 0) {
                    TraceCompat.beginSection("setContents");
                    setContents(mSmartTableAdapter);
                    TraceCompat.endSection();
                }
            }
        },adapter);

    }

    private void measureAsync(final Callback<Void> callback, BaseSmartTableAdapter adapter){
      final int rowCount=adapter.getRowCount();
      final int columnCount=adapter.getColumnCount();
      final int contentCount=adapter.getContentItemCount();
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                if (mShowSidebar && rowCount>0) {
                    for (int i=0;i<rowCount;i++){
                        View sidebarView=mSideBarItemList.get(i).getItemView();
                        mSidebarItemsSize.add(measureSize(sidebarView));
                    }
                }
                if (mShowHeader && columnCount > 0) {
                    for (int i=0;i<columnCount;i++){
                        View headerView=mHeaderItemList.get(i).getItemView();
                        mHeaderItemsSize.add(measureSize(headerView));
                    }
                }
                if (contentCount> 0) {
                    for (int i=0;i<contentCount;i++){
                        View contentView=mContentItemList.get(i).getItemView();
                        mContentItemsSize.add(measureSize(contentView));
                    }
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onComplete(null);
                    }
                });
            }
        });
    }

    /**
     * <p>this method returns an array of ints that value at position 0 is row position
     * and value at position 1 is column position</p>
     */
    public int[] getContentItemPosition(BaseSmartContentItemViewHolder contentItem) {
        int rowCont = mHeaderItemList.size();
        int currentPos = mContentItemList.indexOf(contentItem);
        int rowPos = (currentPos / rowCont);
        int colPos = currentPos % rowCont;
        return new int[]{rowPos, colPos};
    }

    /**
     * Returns table adapter
     * */
    public BaseSmartTableAdapter getAdapter() {
        return mSmartTableAdapter;
    }

    /**
     * Update table with new values
     * */
    @Override
    public void notifyDataSetChanged() {

        updateTable(mSmartTableAdapter);

    }

    @Override
    public void notifyContentItemChanged(int adapterPosition) {
        if (mContentItemList==null ){
            throw new RuntimeException("you dose not have content in table");
        }
        if (mContentItemList.size()<adapterPosition) {
            throw new IndexOutOfBoundsException("adapterPosition that you pass is "+adapterPosition + " but content size is : "+mContentItemList.size());
        }
        int rowCount=mSideBarItemList.size();
        int colCount=mHeaderItemList.size();
        int rowPos=adapterPosition/colCount;
        int colPos=adapterPosition%colCount;
        mSmartTableAdapter.onBindContentViewHolder(mContentItemList.get(adapterPosition),rowPos,colPos);
    }

    @Override
    public void notifyContentItemChanged(int rowPosition, int columnPosition) {
        int colCount=mHeaderItemList.size();
        int adapterPosition=rowPosition*colCount+columnPosition;
        if (mContentItemList==null ){
            throw new RuntimeException("you dose not have content in table");
        }
        if (mContentItemList.size()<adapterPosition) {
            throw new IndexOutOfBoundsException("adapterPosition that you pass is "+adapterPosition + " but content size is : "+mContentItemList.size());
        }
        mSmartTableAdapter.onBindContentViewHolder(mContentItemList.get(adapterPosition),rowPosition,columnPosition);
    }

    @Override
    public void notifyHeaderItemChanged(int position) {
        if (mHeaderItemList==null ){
            throw new RuntimeException("you dose not have header in table");
        }
        if (mHeaderItemList.size()<position) {
            throw new IndexOutOfBoundsException("position that you pass is "+position + " but header size is : "+mHeaderItemList.size());
        }
        mSmartTableAdapter.onBindHeaderViewHolder(mHeaderItemList.get(position),position);
    }

    @Override
    public void notifySidebarItemChanged(int position) {
        if (mSideBarItemList==null ){
            throw new RuntimeException("you dose not have sidebar in table");
        }
        if (mSideBarItemList.size()<position) {
            throw new IndexOutOfBoundsException("position that you pass is "+position + " but sidebar size is : "+mSideBarItemList.size());
        }
        mSmartTableAdapter.onBindSidebarViewHolder(mSideBarItemList.get(position),position);
    }

    /*
     * measure size of view before render it
     * */
    public Pair<Integer, Integer> measureSize(View view) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        if (view.getParent()!=null){
//            ((ViewGroup)view.getParent()).removeView(view);
//        }
//        view.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT));
//        RelativeLayout relativeLayout=new RelativeLayout(getContext());
//        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//                LayoutParams.WRAP_CONTENT));
//        relativeLayout.addView(view);

        //new


        int deviceWidth = metrics.widthPixels;
        int deviceHeight = metrics.heightPixels;
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(deviceWidth, MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(deviceHeight, MeasureSpec.UNSPECIFIED);
//        relativeLayout.measure(widthMeasureSpec, heightMeasureSpec);
//        Pair<Integer, Integer> result=new Pair<>(relativeLayout.getMeasuredWidth(), relativeLayout.getMeasuredHeight());
//        relativeLayout.removeView(view);
//        view.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
        //new
        view.measure(widthMeasureSpec,heightMeasureSpec);
        Pair<Integer, Integer> result=new Pair<>(view.getMeasuredWidth(), view.getMeasuredHeight());
        return result;
    }

    private int getMaxWidth(List<Pair<Integer, Integer>> pairs) {
        if(pairs==null || pairs.size()==0){
            return 0;
        }
        Pair<Integer, Integer> maxPair = Collections.max(pairs, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
                return p1.first - p2.first;
            }
        });
        return maxPair.first;
    }

    private int getMaxHeight(List<Pair<Integer, Integer>> pairs) {
        if(pairs==null || pairs.size()==0){
            return 0;
        }
        Pair<Integer, Integer> maxPair = Collections.max(pairs, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
                return p1.second - p2.second;
            }
        });
        return maxPair.second;
    }

    private int calculateWidthToFitScreen(){
        int sidebarWidth=getMaxWidth(mSidebarItemsSize);
        int deviceWidth=getResources().getDisplayMetrics().widthPixels;
        int paddingSize=mHeaderItemsSize.size()*verticalDividerWidth;
        int[] contentSize ={deviceWidth-sidebarWidth-paddingSize};
        mStretchToFitDeletedPosition.clear();
        List<Integer> sizeTemp=new ArrayList<>(maxColumnsWidthList);
        if ( sizeTemp.size()==0){
            return 0;
        }
        int sum=0;
        for (Integer i:sizeTemp){
            sum+=i;
        }
            if (contentSize[0]<sum){
                return -2;
            }

        int finalCellSize=-2;
        while (finalCellSize==-2){
            finalCellSize=canSetSumToMaxWidth(sizeTemp,contentSize);
        }

        return finalCellSize;
    }
    private int canSetSumToMaxWidth(List<Integer> sizeTemp,int[] contentSize){
        if (sizeTemp==null || sizeTemp.size()==0){
            return 0;
        }

        Integer max= Collections.max(sizeTemp);
        Integer min=Collections.min(sizeTemp);
        if (min.equals(max)){
            return getContentAverageWidth(sizeTemp.size());
        }
        int leftOverSize=contentSize[0]-max;
        int maxPos=sizeTemp.indexOf(max);
        mStretchToFitDeletedPosition.add(maxPos);
        sizeTemp.remove(max);
        int cellSize=leftOverSize/sizeTemp.size();
        for (Integer w:sizeTemp){
            if (cellSize<w){
                /*calculate again*/
                contentSize[0]=leftOverSize;
//                canSetSumToMaxWidth(sizeTemp,leftOverSize,finalSellSize);
                return -2;

            }
        }
        return cellSize;
    }
    /*for excluding to swt width*/
    private List<Integer> mStretchToFitDeletedPosition=new ArrayList<>();
    /*
     * Setters and Getters
     * */

    /**
     * Set onClickListener for every items in sidebar
     * */
    public void setOnSidebarItemClickListener(OnSidebarItemClickListener sidebarItemClickListener) {
        mSidebarItemClickListener = sidebarItemClickListener;
    }

    /**
     * Set onClickListener for every items in header
     * */
    public void setOnHeaderItemClickListener(OnHeaderItemClickListener headerItemClickListener) {
        mHeaderItemClickListener = headerItemClickListener;
    }
    /**
     * Set onClickListener for every items in content
     * */
    public void setOnContentItemClickListener(OnContentItemClickListener contentItemClickListener) {
        mContentItemClickListener = contentItemClickListener;
    }
    /**
     * Set onLongClickListener for every items in sidebar
     * */
    public void setOnSidebarItemLongClickListener(OnSidebarItemLongClickListener sidebarItemLongClickListener) {
        mSidebarItemLongClickListener = sidebarItemLongClickListener;
    }
    /**
     * Set onLongClickListener for every items in header
     * */
    public void setOnHeaderItemLongClickListener(OnHeaderItemLongClickListener headerItemLongClickListener) {
        mHeaderItemLongClickListener = headerItemLongClickListener;
    }
    /**
     * Set onLongClickListener for every items in content
     * */
    public void setOnContentItemLongClickListener(OnContentItemLongClickListener contentItemLongClickListener) {
        mContentItemLongClickListener = contentItemLongClickListener;
    }
    /**
     * return position of items in sidebar
     * */
    public int getSidebarItemPosition(BaseSmartSidebarItemViewHolder sidebarItem) {
        return mSideBarItemList.indexOf(sidebarItem);
    }
    /**
     * return position of items in header
     * */
    public int getHeaderItemPosition(BaseSmartHeaderItemViewHolder headerItem) {
        return mHeaderItemList.indexOf(headerItem);
    }

    /**
     * return true if sidebar not hidden
     * */
    public boolean isSidebarShowing() {
        return mShowSidebar;
    }
    /**
     * return true if header not hidden
     * */
    public boolean isHeaderShowing() {
        return mShowHeader;
    }
    /**
     * set background for every item in sidebar
     * */
    public void setSidebarItemBackgroundDrawable(Drawable drawable) {
        mSidebarItemBackgroundDrawable = drawable;
    }
    /**
     * set background for every item in header
     * */
    public void setHeaderItemBackgroundDrawable(Drawable drawable) {
        mHeaderItemBackgroundDrawable = drawable;
    }
    /**
     * set background for every item in content
     * */
    public void setContentItemBackgroundDrawable(Drawable drawable) {
        mContentItemBackgroundDrawable = drawable;
    }
    /**
     * Set adapter for table.
     * After setting adapter, the table will be updated
     * */
    public void setAdapter(BaseSmartTableAdapter adapter) {
        if (adapter == null) {
            throw new RuntimeException("SmartTableView's adapter is null");
        }
        mSmartTableAdapter = adapter;
        mSmartTableAdapter.setNotifyObserver(this);
        updateTable(adapter);
    }

    /**
     * Show or hide sidebar
     * @param showSidebar if false the sidebar will be disappear
     * */
    public void setShowSidebar(boolean showSidebar) {
        mShowSidebar = showSidebar;
        int visibility = showSidebar ? VISIBLE : GONE;
        mVerticalTableSplitterView.setVisibility(visibility);
        sidebarScrollView.setVisibility(visibility);
        cornerTopRightContainer.setVisibility(visibility);
    }


    /**
     * Show or hide sidebar
     * @param showHeader if false the header will be disappear
     * */
    public void setShowHeader(boolean showHeader) {
        mShowHeader = showHeader;
        int visibility = showHeader ? VISIBLE : GONE;
        mHorizontalTableSplitterView.setVisibility(visibility);
        headerScrollView.setVisibility(visibility);
        cornerTopRightContainer.setVisibility(visibility);

    }

    private void showHeaderView(boolean showHeader) {
        int visibility = showHeader ? VISIBLE : GONE;
        if (headerScrollView.getVisibility()!=visibility)
        headerScrollView.setVisibility(visibility);

    }

    private void showSidebarView(boolean showSidebar) {
        int visibility = showSidebar ? VISIBLE : GONE;
        if (sidebarScrollView.getVisibility()!=visibility)
        sidebarScrollView.setVisibility(visibility);
    }

    private void showContentView(boolean showContent) {
        int visibility = showContent ? VISIBLE : GONE;
        if (contentVerticalScrollView.getVisibility()!=visibility)
        contentVerticalScrollView.setVisibility(visibility);
    }

    private boolean contentIsShowing() {
        return contentVerticalScrollView.getVisibility() == VISIBLE;
    }

    public void setStretchIfContentNotFitScreen(boolean stretchIfContentNotFitScreen) {
        mStretchIfContentNotFitScreen = stretchIfContentNotFitScreen;
    }

    /**
     * Set the hole table width
    * */
    public void setLayoutWidth(@Px int width) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        setLayoutParams(params);
    }
    /**
     * Set the hole table height
     * */
    public void setLayoutHeight(@Px int height) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = height;
        setLayoutParams(params);
    }
    /**
     * Returns the hole table width
     * */
    public int getLayoutWidth() {
        return getWidth();
    }
    /**
     * Returns the hole table height
     * */
    public int getLayoutHeight() {
        return getHeight();
    }
    /**
     * Returns the view set by {@link #setCornerTopView(int)}
     * */
    public View getCornerTopRightView() {
        return cornerTopRightView;
    }
    /**
     * Returns the Drawable set by {@link #setHorizontalContentDivider(Drawable)}
     * */
    public Drawable getHorizontalContentDivider() {
        return mHorizontalContentDivider;
    }
    /**
     * Set a Drawable for horizontal divider of every cell
     * */
    public void setHorizontalContentDivider(Drawable horizontalContentDivider) {
        mHorizontalContentDivider = horizontalContentDivider;
    }
    /**
     * Returns the Drawable set by {@link #setVerticalContentDivider(Drawable)}
     * */
    public Drawable getVerticalContentDivider() {
        return mVerticalContentDivider;
    }
    /**
     * Set a Drawable for vertical divider of every cell
     * */
    public void setVerticalContentDivider(Drawable drawable) {
        mVerticalContentDivider = drawable;
    }
    /**
     * Set a height of horizontal divider in pixel
     * @param height in pixel format size
     * */
    public void setHorizontalDividerHeight(int height) {
        this.horizontalDividerHeight = height;
    }
    /**
     * Returns the height of horizontal divider
     * */
    public int getHorizontalDividerHeight() {
        return horizontalDividerHeight;
    }
    /**
     * Set a width of vertical divider in pixel
     * @param width in pixel format size
     * */
    public void setVerticalDividerWidth(int width) {
        this.verticalDividerWidth = width;
    }
    /**
     * Returns the width of vertical divider
     * */
    public int getVerticalDividerWidth() {
        return verticalDividerWidth;
    }

    /**
     * Set a drawable for horizontal table splitter divider, that splits header from tow other sides
     * */
    public void setHorizontalTableSplitter(Drawable drawable) {
        mHorizontalTableSplitter = drawable;
        mHorizontalTableSplitterView.setBackground(mHorizontalTableSplitter);
    }

    /**
     * Set a drawable for vertical table splitter divider, that splits sidebar from tow other sides
     * */
    public void setVerticalTableSplitter(Drawable drawable) {
        mVerticalTableSplitter = drawable;
        mVerticalTableSplitterView.setBackground(mVerticalTableSplitter);
    }
    /**
     * Set height of horizontal table splitter divider, that splits header from tow other sides
     * */
    public void setHorizontalTableSplitterHeight(int height) {
        this.horizontalTableSplitterHeight = height;
        ViewGroup.LayoutParams params = mHorizontalTableSplitterView.getLayoutParams();
        params.height = horizontalTableSplitterHeight;
        mHorizontalTableSplitterView.setLayoutParams(params);
    }
    /**
     * Set width of vertical table splitter divider, that splits sidebar from tow other sides
     * */
    public void setVerticalTableSplitterWidth(int width) {
        this.verticalTableSplitterWidth = width;
        ViewGroup.LayoutParams params = mVerticalTableSplitterView.getLayoutParams();
        params.width = width;
        mVerticalTableSplitterView.setLayoutParams(params);
    }
    /**
     * Set a view for corner view . corner view is a view that located above sidebar and beside header
     * @param layoutId id of layout or view that should replace with default view
     * */
    public void setCornerTopView(@LayoutRes int layoutId) {
        this.cornerTopRightResourceId = layoutId;
        this.cornerTopRightView = LayoutInflater.from(getContext()).inflate(layoutId, cornerTopRightContainer, false);
        /* set topCornerView if set*/
        cornerTopRightContainer.removeAllViewsInLayout();
        cornerTopRightContainer.addView(cornerTopRightView);

    }
    /** Sets the header and content to fit hole table width*/
    public void setFitHorizontally(boolean fitHorizontally){
        mFitHorizontally=fitHorizontally;
    }
    /**
     * Set background color for corner view ,corner view is a view that located above sidebar and beside header
     * @param color color int of a color
     * */
    public void setCornerTopBackgroundColor(@ColorInt int color) {
        cornerTopRightContainer.setBackgroundColor(color);
    }
    /**
     * Set background color for corner view ,corner view is a view that located above sidebar and beside header
     * @param color in html color format for example #FFFFFF
     * */
    public void setCornerTopBackgroundColor(final String color) {
        int colorInt = Color.parseColor(color);
        setCornerTopBackgroundColor(colorInt);
    }
    /**
     * Set background color for corner view ,corner view is a view that located above sidebar and beside header
     * */
    public void setCornerTopBackgroundDrawable(Drawable drawable) {
        cornerTopRightContainer.setBackground(drawable);
    }

    public void setCornerTopViewClickListener(OnClickListener clickListener){
        cornerTopRightContainer.setOnClickListener(clickListener);
    }

    /**if you want to fit all contents or headers to max width of them width set true*/
    public void setFitToMaxWidth(boolean fitToMaxWidth) {
        mFitToMaxWidth = fitToMaxWidth;
    }
    /**
     * Returns the header background drawable set by {@link #setHeaderBackground(Drawable)}
     * */
    public Drawable getHeaderBackground() {
        return mHeaderBackground;
    }
    /**
     * @param headerBackground Sets the header part and corner top part background
     * */
    public void setHeaderBackground(Drawable headerBackground) {
        mHeaderBackground = headerBackground;
        headerScrollView.setBackground(headerBackground);
        setCornerTopBackgroundDrawable(headerBackground);
    }
    /** if you want to stretch content items width to header width set true else set false*/
    public void setStretchContentToHeaderWidth(boolean stretchContentToHeaderWidth) {
        mStretchContentToHeaderWidth = stretchContentToHeaderWidth;
    }
    /** if you want to stretch header items width to content width set true else set false*/
    public void setStretchHeaderToContentWidth(boolean stretchHeaderToContentWidth) {
        mStretchHeaderToContentWidth = stretchHeaderToContentWidth;
    }

    /**
     * Returns the sidebar background drawable set by {@link #setSidebarBackground(Drawable)}
     * */
    public Drawable getSidebarBackground() {
        return mSidebarBackground;
    }
    /**
     * @param sidebarBackground Sets the sidebar part background
     * */
    public void setSidebarBackground(Drawable sidebarBackground) {
        mSidebarBackground = sidebarBackground;
        sidebarScrollView.setBackground(sidebarBackground);
    }
    /**
     * Returns the content background drawable set by {@link #setContentBackground(Drawable)}
     * */
    public Drawable getContentBackground() {
        return mContentBackground;
    }
    /**
     * @param contentBackground Sets the content part background
     * */
    public void setContentBackground(Drawable contentBackground) {
        mContentBackground = contentBackground;
        contentHorizontalScrollView.setBackground(contentBackground);
    }

    public Drawable getHorizontalTableSplitter() {
        return mHorizontalTableSplitter;
    }

    public Drawable getVerticalTableSplitter() {
        return mVerticalTableSplitter;
    }

    public int getHorizontalTableSplitterHeight() {
        return horizontalTableSplitterHeight;
    }

    public int getVerticalTableSplitterWidth() {
        return verticalTableSplitterWidth;
    }

    public Drawable getHorizontalSidebarDivider() {
        return mHorizontalSidebarDivider;
    }

    public void setHorizontalSidebarDivider(Drawable horizontalSidebarDivider) {
        mHorizontalSidebarDivider = horizontalSidebarDivider;
        if (mHorizontalContentDivider != null) {
            horizontalDividerHeight = mHorizontalContentDivider.getIntrinsicHeight();
        }

    }

    public Drawable getVerticalHeaderDivider() {
        return mVerticalHeaderDivider;
    }

    public void setVerticalHeaderDivider(Drawable verticalHeaderDivider) {
        mVerticalHeaderDivider = verticalHeaderDivider;
        if (mVerticalContentDivider != null) {
            verticalDividerWidth = mVerticalContentDivider.getIntrinsicWidth();
        }
    }
}
