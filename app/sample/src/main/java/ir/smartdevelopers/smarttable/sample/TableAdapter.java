package ir.smartdevelopers.smarttable.sample;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import ir.smartdevelopers.smarttable.sample.models.CellModel;
import ir.smartdevelopers.smarttable.views.TableView.SmartBaseTableAdapter;
import ir.smartdevelopers.smarttable.views.TableView.SmartContentViewHolder;
import ir.smartdevelopers.smarttable.views.TableView.SmartHeaderViewHolder;
import ir.smartdevelopers.smarttable.views.TableView.SmartSidebarViewHolder;
import ir.smartdevelopers.smarttable.views.TableView.SmartTableUtil;

public class TableAdapter extends SmartBaseTableAdapter<TableAdapter.HeaderVH, TableAdapter.SideVH, TableAdapter.CellVH> {

    private Context mContext;
    private List<String> mHeaders;
    private List<String> mSidebars;
    private List<CellModel> mCellModels;

    public TableAdapter(Context context, List<String> headers, List<String> sidebars, List<CellModel> cellModels) {
        mContext = context;
        mHeaders = headers;
        mSidebars = sidebars;
        mCellModels = cellModels;
    }

    @Override
    public HeaderVH onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
        return new HeaderVH(view);
    }

    @Override
    public SideVH onCreateSidebarViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sidebar, parent, false);
        return new SideVH(view);
    }

    @Override
    public CellVH onCreateContentViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cell, parent, false);
        return new CellVH(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderVH headerHolder, int position) {
        headerHolder.bindView(mHeaders.get(position));
    }

    @Override
    public void onBindSidebarViewHolder(SideVH sidebarHolder, int position) {
        sidebarHolder.bindView(mSidebars.get(position));
    }

    @Override
    public void onBindContentViewHolder(CellVH contentHolder, int adapterPosition, int rowPosition, int columnPosition) {
        contentHolder.bindView(mCellModels.get(adapterPosition));
    }

    @Override
    public int getRowCount() {
        return mSidebars.size();
    }

    @Override
    public int getColumnCount() {
        return mHeaders.size();
    }

    @Override
    public int getContentCount() {
        return mCellModels.size();
    }

    /**
     * If we want each column has flexible width we must calculate it, otherwise we can return
     * any fixed number
     */
    @Override
    protected int getMaxColumnWidth(int position) {

        ViewGroup parent = new LinearLayout(mContext);
        parent.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        int maxHeaderWidth = 0;
        HeaderVH headerVH = onCreateHeaderViewHolder(parent, 0);
        headerVH.bindView(mHeaders.get(position));
        maxHeaderWidth = SmartTableUtil.measureWidth(headerVH.itemView);

        /* we don't want to measure all column views it is too heavy work
         * so we first find biggest content then measure it's view */
        Paint paint = new Paint();
        int biggestTextPosition = -1;
        float biggestLength = 0;
        int rowCount = getRowCount();
        int columnCount = getColumnCount();

        for (int row = 0; row < rowCount; row++) {
            int itemPos = SmartTableUtil.getAdapterPosition(row, position, columnCount);
            String title = mCellModels.get(itemPos).title;
            String subTitle = mCellModels.get(itemPos).subTitle;

            float textSize = 0;
            float titleSize = 0;
            float subTitleSize = 0;
            // we can not use title.length() for measuring biggest text size because
            // every character has different size
            if (!TextUtils.isEmpty(title)) {
                titleSize = paint.measureText(title);
            }
            if (!TextUtils.isEmpty(subTitle)) {
                subTitleSize = paint.measureText(subTitle);
            }
            textSize = Math.max(titleSize, subTitleSize);
            if (textSize > biggestLength) {
                biggestLength = textSize;
                biggestTextPosition = itemPos;
            }
        }
        int maxRowWidth = 0;
        if (biggestTextPosition != -1) {
            CellVH cellVH = onCreateContentViewHolder(parent, 0);
            // measuring biggest content
            cellVH.bindView(mCellModels.get(biggestTextPosition));
            maxRowWidth = SmartTableUtil.measureWidth(cellVH.itemView);
        }
        return Math.max(maxHeaderWidth, maxRowWidth);
    }

    class HeaderVH extends SmartHeaderViewHolder {
        private TextView txtHeader;

        public HeaderVH(@NonNull View itemView) {
            super(itemView);
            txtHeader = itemView.findViewById(R.id.txtHeader);
        }

        public void bindView(String headerText) {
            txtHeader.setText(headerText);
        }
    }

    class SideVH extends SmartSidebarViewHolder {
        private TextView txtSidebarText;

        public SideVH(@NonNull View itemView) {
            super(itemView);
            txtSidebarText = itemView.findViewById(R.id.txtSidebar);
        }

        public void bindView(String headerText) {
            txtSidebarText.setText(headerText);
        }
    }

    class CellVH extends SmartContentViewHolder {
        private TextView txtTitle;
        private TextView txtSubTitle;

        public CellVH(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtCellTitle);
            txtSubTitle = itemView.findViewById(R.id.txtCellSubtitle);
        }

        public void bindView(CellModel model) {
            txtTitle.setText(model.title);
            txtSubTitle.setText(model.subTitle);
        }
    }
}
