package ir.smartdevelopers.smarttable.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import ir.smartdevelopers.smarttable.sample.models.CellModel;
import ir.smartdevelopers.smarttable.views.TableView.SmartTableView;

public class MainActivity extends AppCompatActivity {

    private final List<String> headers = new ArrayList<>();
    private final List<String> sidebars = new ArrayList<>();
    private final List<CellModel> cells = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SmartTableView tableView = findViewById(R.id.tableView);

        fillLists();
        // you can add custom divider or set showDefaultDivider in xlm
//        tableView.addColumnItemDecoration(new DefaultTableVerticalDivider(this,DefaultTableVerticalDivider.HORIZONTAL),
//                new DefaultTableVerticalDivider(this,DefaultTableVerticalDivider.HORIZONTAL));
//        tableView.addRowItemDecoration(new DefaultTableHorizontalDivider(this, DividerItemDecoration.VERTICAL),
//                new DefaultTableHorizontalDivider(this, DividerItemDecoration.VERTICAL));
        TableAdapter adapter = new TableAdapter(this,headers,sidebars,cells);
        tableView.setAdapter(adapter);
        // if you want access corner view
        // find any view tha you put in it by find viewById
        View cornerView = tableView.getCornerView();
        if (cornerView != null){
            ImageView imgCorner = cornerView.findViewById(R.id.imgCorner);
            // do something
        }


    }

    private void fillLists() {
        int columnCount = 20;
        int rowCount = 40;
        int cellCount = columnCount * rowCount;
        for (int i = 0; i < columnCount; i++) {
            headers.add("Header " + i);
        }
        for (int i = 0; i < rowCount; i++) {
            sidebars.add("Sidebar " + i);
        }
        for (int i = 0; i < cellCount; i++) {
            CellModel model = new CellModel();
            if (i % 3 == 0) {
                model.title = "This is big title " + i;
                model.subTitle = "Subtitle " + i;
            } else if (i % 7 == 0) {
                model.title = "Title " + i;
                model.subTitle = "This is big subtitle " + i;
            } else {
                model.title = "Title " + i;
                model.subTitle = "Subtitle " + i;
            }
            cells.add(model);
        }
    }

}