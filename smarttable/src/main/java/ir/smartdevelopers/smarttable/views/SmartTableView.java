package ir.smartdevelopers.smarttable.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.annotation.Px;
import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ir.smartdevelopers.smarttable.R;
import ir.smartdevelopers.smarttable.views.listeners.OnContentItemClickListener;
import ir.smartdevelopers.smarttable.views.listeners.OnContentItemLongClickListener;
import ir.smartdevelopers.smarttable.views.listeners.OnHeaderItemClickListener;
import ir.smartdevelopers.smarttable.views.listeners.OnHeaderItemLongClickListener;
import ir.smartdevelopers.smarttable.views.listeners.OnSidebarItemClickListener;
import ir.smartdevelopers.smarttable.views.listeners.OnSidebarItemLongClickListener;

public class SmartTableView extends RelativeLayout implements NotifyObserver {
    private NestedScrollView sidebarScrollView, contentVerticalScrollView;
    private SmartHorizontalScrollView headerScrollView, contentHorizontalScrollView;

    private RelativeLayout root;
    private TableLayout mSidebarTableLayout;
    private TableLayout mContentTableLayout;
    private TableRow mHeaderTableRow;
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
    private Drawable mHorizontalDivider;
    private Drawable mVerticalDivider;
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
        root = findViewById(R.id.table_root);
        mHeaderTableRow = findViewById(R.id.header_tableRow);
        mHorizontalTableSplitterView = findViewById(R.id.horizontal_line);
        mVerticalTableSplitterView = findViewById(R.id.vertical_line);
        horizontalDividerHeight = getResources().getDimensionPixelSize(R.dimen.smart_table_default_horizontal_divider_height);
        verticalDividerWidth = getResources().getDimensionPixelSize(R.dimen.smart_table_default_vertical_divider_width);

        manageScrollViews();

        if (attrs != null) {

            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartTableView);

            /* layout sizes*/
            int layoutWidth = typedArray.getLayoutDimension(R.styleable.SmartTableView_android_layout_width,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            setLayoutWidth(layoutWidth);
            int layoutHeight = typedArray.getLayoutDimension(R.styleable.SmartTableView_android_layout_height,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            setLayoutHeight(layoutHeight);

            horizontalDividerHeight = typedArray.getDimensionPixelSize(R.styleable.SmartTableView_sidebarItemBottomMargin,
                    getResources().getDimensionPixelSize(R.dimen.smart_table_default_horizontal_divider_height));
            verticalDividerWidth = typedArray.getDimensionPixelSize(R.styleable.SmartTableView_headerBetweenItemMargin,
                    getResources().getDimensionPixelSize(R.dimen.smart_table_default_vertical_divider_width));

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

        for (int row = 0; row < size; row++) {
            final BaseSmartSidebarItemViewHolder sidebarViewHolder = mSideBarItemList.get(row);
            TableRow tableRow = (TableRow) sidebarViewHolder.getParent();

            /* get max size */
            int maxWidth = getMaxWidth(mSidebarItemsSize);
            int maxHeight = maxRowHeightList.get(row);
            /* set size for sidebar items before rendering*/
            sidebarViewHolder.setLayoutWidth(maxWidth);
            sidebarViewHolder.setLayoutHeight(maxHeight);
            View sidebarView = sidebarViewHolder.getItemView();
            tableRow.removeViewInLayout(sidebarView);
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
                tableRow.setBackground(adapter.getSidebarRowBackground(sidebarPosFinal));
            }

            if (sidebarView.getParent()!=null){
                ((ViewGroup)sidebarView.getParent()).removeView(sidebarView);
            }
            tableRow.addView(sidebarView);
           if (tableRow.getParent()!=null){
                ((ViewGroup)tableRow.getParent()).removeView(tableRow);
            }
            mSidebarTableLayout.addView(tableRow);
            /* set horizontal divider */
            if (horizontalDividerHeight != 0) {
                if (row < size - 1) {
                    View horizontalDivider = new View(getContext());
                    horizontalDivider.setLayoutParams(new TableRow.
                            LayoutParams(maxWidth, horizontalDividerHeight
                    ));
                    if (mHorizontalDivider != null) {
                        horizontalDivider.setBackground(mHorizontalDivider);
                    }
                    mSidebarTableLayout.addView(horizontalDivider);
                }
            }


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

        for (int col = 0; col < size; col++) {
            final BaseSmartHeaderItemViewHolder headerViewHolder = mHeaderItemList.get(col);

            /* get max header items height*/
            int maxHeight = getMaxHeight(mHeaderItemsSize);
            /* set max height to all header items*/
            headerViewHolder.setLayoutHeight(maxHeight);
            /* set max width to headerViewHolder*/
            headerViewHolder.setLayoutWidth(maxColumnsWidthList.get(col));
            View headerView=headerViewHolder.getItemView();
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

            /* header divider */
            if (verticalDividerWidth != 0) {
                if (col < size - 1) {
                    View verticalDivider = new View(getContext());
                    verticalDivider.setLayoutParams(new TableRow.
                            LayoutParams(verticalDividerWidth, maxHeight));
                    if (mVerticalDivider != null) {
                        verticalDivider.setBackground(mVerticalDivider);
                    }
                    mHeaderTableRow.addView(verticalDivider);
                }
            }
        }
    }

    /*
     * this method called after setHeader and fill content
     * the content must be the same size of its header column and its sidebar row
     * for each sidebar item one tableRow
     * for each header item one content
     * */
    private void setContents(BaseSmartTableAdapter adapter) {


        final int numOfSidebarItem = adapter.getRowCount();
        final int numOfHeaderItem = adapter.getColumnCount();
        mContentTableLayout.removeAllViewsInLayout();

        int index = 0;
        for (int row = 0; row < numOfSidebarItem; row++) {
            TableRow tableRow = (TableRow) mContentItemList.get(index)
                    .getParent();

            if (adapter.getContentRowBackground(row) != null) {
                tableRow.setBackground(adapter.getContentRowBackground(row));
            }
            /*remove all chide from tableRow to add dividers between childes */
            tableRow.removeAllViews();
            int maxHeight = maxRowHeightList.get(row);
            for (int col = 0; col < numOfHeaderItem; col++) {

                final int rowPos=row;
                final int colPos=col;
                final BaseSmartContentItemViewHolder contentViewHolder = mContentItemList.get(index);
                /* get max width of column*/
                int maxWidth = maxColumnsWidthList.get(col);
                /* set max size  to contents*/
                contentViewHolder.setLayoutHeight(maxHeight);
                contentViewHolder.setLayoutWidth(maxWidth);

                /* add contentViewHolder to table row*/
                View contentView = contentViewHolder.getItemView();

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

                tableRow.addView(contentView);
                if (verticalDividerWidth != 0) {
                    if (col < numOfHeaderItem - 1) {

                        View verticalDivider = new View(getContext());
                        verticalDivider.setLayoutParams(new TableRow.
                                LayoutParams(verticalDividerWidth, maxHeight));
                        if (mVerticalDivider != null) {
                            verticalDivider.setBackground(mVerticalDivider);
                        }

                        tableRow.addView(verticalDivider);
                    }
                }
                index++;
            }

            mContentTableLayout.addView(tableRow);
            /* add divider */
            if (horizontalDividerHeight != 0) {
                View horizontalDivider = new View(getContext());

                horizontalDivider.setLayoutParams(new TableLayout.
                        LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, horizontalDividerHeight
                ));
                if (mHorizontalDivider != null) {
                    horizontalDivider.setBackground(mHorizontalDivider);
                }
                mContentTableLayout.addView(horizontalDivider);
            }
        }

    }

    @SuppressWarnings("unchecked")
    private List<BaseSmartSidebarItemViewHolder> generateSidebarItems(BaseSmartTableAdapter adapter) {
        int size = adapter.getRowCount();
        /* create side bar measure size array*/
        mSidebarItemsSize = new ArrayList<>(size);
        List<BaseSmartSidebarItemViewHolder> smartSidebarItemList = new ArrayList<>(size);
        /* get all items and measure view size*/
        for (int i = 0; i < size; i++) {
            TableRow tableRow;
            if (mSideBarItemList.size() > i) {
                ViewParent parent = mSideBarItemList.get(i).getParent();
                tableRow = (TableRow) parent;
            } else {
                tableRow = new TableRow(getContext());
            }
            /*
             *generate BaseSmartSidebarItem from adapter by calling
             *adapter.onCreateSideBarItem
             */
            final BaseSmartSidebarItemViewHolder sidebarViewHolder = getSidebarItemViewHolder(tableRow,
                    adapter, i);
            /*set tableRow as sidebarViewHolder parent to fetch it later */
            sidebarViewHolder.setParent(tableRow);
            /* call onBind after view holder created */
            adapter.onBindSidebarViewHolder(sidebarViewHolder, i);

            View sidebarView = sidebarViewHolder.getItemView();

            /* measure size*/
            mSidebarItemsSize.add(measureSize(sidebarView));

            /* add BaseSmartSidebarItem to mSideBarItemList*/
            smartSidebarItemList.add(sidebarViewHolder);
        }
        return smartSidebarItemList;
    }

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
            mHeaderItemsSize.add(measureSize(headerView));

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
                mContentItemsSize.add(measureSize(contentView));

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
        }
        oldSidebarViewHolder.setAdapterPosition(position);
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
        }
        oldHeaderViewHolder.setAdapterPosition(position);
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
        }
        oldContentViewHolder.setAdapterPosition(index);
        oldContentViewHolder.setContentPosition(rowPosition, colPosition);
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

        if (mFitHorizontally){
            int start=(int) mHeaderTableRow.getX();

        }

        /* if contents is empty just return header width*/
        if (contentItemsSizeList == null || contentItemsSizeList.isEmpty()) {
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
        /* if header is empty just return contents width*/
        if (headerItemsSizeList == null || headerItemsSizeList.isEmpty()) {
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

    private void updateTable(BaseSmartTableAdapter adapter) {
        if (mShowSidebar || adapter.getRowCount() > 0) {
            if (adapter.getRowCount() == 1) {
                showSidebarView(true);
            }
            mSideBarItemList = generateSidebarItems(adapter);

        } else {
            mSideBarItemList = Collections.emptyList();
            mSidebarItemsSize = Collections.emptyList();
            showSidebarView(false);
        }
        if (mShowHeader || adapter.getColumnCount() > 0) {
            if (adapter.getColumnCount() == 1) {
                showHeaderView(true);
            }
            mHeaderItemList = generateHeaderItems(adapter);
        } else {
            mHeaderItemList = Collections.emptyList();
            mHeaderItemsSize = Collections.emptyList();
            showHeaderView(false);
        }
        if (adapter.getContentItemCount() > 0) {
            if (!contentIsShowing()) {
                showContentView(true);
            }
            mContentItemList = generateContentItems(adapter);

        } else {
            mContentItemList = Collections.emptyList();
            mContentItemsSize = Collections.emptyList();
            showContentView(false);
        }

        maxRowHeightList = generateMaxRowHeight(adapter.getRowCount(), mSidebarItemsSize, mContentItemsSize);
        maxColumnsWidthList = generateMaxColumnWidth(adapter.getColumnCount(), mHeaderItemsSize, mContentItemsSize);
        if (mShowSidebar || adapter.getRowCount() > 0) {
            setSidebarItems(mSmartTableAdapter);
        }
        if (mShowHeader || adapter.getColumnCount() > 0) {
            setHeaderItems(mSmartTableAdapter);
        }
        if (adapter.getContentItemCount() > 0) {
            setContents(mSmartTableAdapter);
        }
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

    /*
     * measure size of view before render it
     * */
    private Pair<Integer, Integer> measureSize(View view) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (view.getParent()!=null){
            ((ViewGroup)view.getParent()).removeView(view);
        }
        view.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        RelativeLayout relativeLayout=new RelativeLayout(getContext());
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        relativeLayout.addView(view);
        int deviceWidth = metrics.widthPixels;
        int deviceHeight = metrics.heightPixels;
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(deviceWidth, MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(deviceHeight, MeasureSpec.UNSPECIFIED);
        relativeLayout.measure(widthMeasureSpec, heightMeasureSpec);
        Pair<Integer, Integer> result=new Pair<>(relativeLayout.getMeasuredWidth(), relativeLayout.getMeasuredHeight());
        relativeLayout.removeView(view);
        view.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return result;
    }

    private int getMaxWidth(List<Pair<Integer, Integer>> pairs) {
        Pair<Integer, Integer> maxPair = Collections.max(pairs, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
                return p1.first - p2.first;
            }
        });
        return maxPair.first;
    }

    private int getMaxHeight(List<Pair<Integer, Integer>> pairs) {
        Pair<Integer, Integer> maxPair = Collections.max(pairs, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
                return p1.second - p2.second;
            }
        });
        return maxPair.second;
    }


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
        headerScrollView.setVisibility(visibility);

    }

    private void showSidebarView(boolean showSidebar) {
        int visibility = showSidebar ? VISIBLE : GONE;
        sidebarScrollView.setVisibility(visibility);
    }

    private void showContentView(boolean showContent) {
        int visibility = showContent ? VISIBLE : GONE;
        contentVerticalScrollView.setVisibility(visibility);
    }

    private boolean contentIsShowing() {
        return contentVerticalScrollView.getVisibility() == VISIBLE;
    }

    /**
     * Set the hole table width
    * */
    public void setLayoutWidth(@Px int width) {
        ViewGroup.LayoutParams params = root.getLayoutParams();
        params.width = width;
        root.setLayoutParams(params);
    }
    /**
     * Set the hole table height
     * */
    public void setLayoutHeight(@Px int height) {
        ViewGroup.LayoutParams params = root.getLayoutParams();
        params.height = height;
        root.setLayoutParams(params);
    }
    /**
     * Returns the hole table width
     * */
    public int getLayoutWidth() {
        return root.getWidth();
    }
    /**
     * Returns the hole table height
     * */
    public int getLayoutHeight() {
        return root.getHeight();
    }
    /**
     * Returns the view set by {@link #setCornerTopView(int)}
     * */
    public View getCornerTopRightView() {
        return cornerTopRightView;
    }
    /**
     * Returns the Drawable set by {@link #setHorizontalDivider(Drawable)}
     * */
    public Drawable getHorizontalDivider() {
        return mHorizontalDivider;
    }
    /**
     * Set a Drawable for horizontal divider of every cell
     * */
    public void setHorizontalDivider(Drawable horizontalDivider) {
        mHorizontalDivider = horizontalDivider;
    }
    /**
     * Returns the Drawable set by {@link #setVerticalDivider(Drawable)}
     * */
    public Drawable getVerticalDivider() {
        return mVerticalDivider;
    }
    /**
     * Set a Drawable for vertical divider of every cell
     * */
    public void setVerticalDivider(Drawable drawable) {
        mVerticalDivider = drawable;
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
        if (fitHorizontally){

        }else {

        }
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
}
