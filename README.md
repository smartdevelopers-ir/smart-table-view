# SmartTableView
This is a crossTab Table view .

<img src="images/smart_table.gif" width="250">

# Installation
* Add it in your root build.gradle at the end of repositories:
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
* Add the dependency
```gradle
	dependencies {
	        implementation 'com.github.smartdevelopers-ir:smart-table-view:Tag'
	}
```

# Impelemention

* Add SmartTableView in your layout.xlm file
```xlm
  <ir.smartdevelopers.smarttable.views.TableView.SmartTableView
        android:id="@+id/tableView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_marginTop="8dp"
        app:layout_constraintTop_toBottomOf="@id/appbar"
        app:layout_constraintBottom_toBottomOf="parent"
        app:cornerTopRightView="@layout/corner_view"
        app:showDefaultDividers="true"
        />
```
* set adapter to table view like RecyclerView
```java
 SmartTableView tableView = findViewById(R.id.tableView);
 TableAdapter adapter = new TableAdapter(this,headers,sidebars,cells);
 tableView.setAdapter(adapter);
```

You can see sample for implementian of TableAdapter and more.

# All xml attributes
```xlm
 <ir.smartdevelopers.smarttable.views.TableView.SmartTableView
        ...
        app:cornerTopRightView="@layout/corner_view"
        app:showDefaultDividers="true"
        app:cornerTopRightColor="#eee"
        app:showSidebar="true"
        app:showHeader="true"
        app:fitHorizontally="true"
        app:stretchIfContentNotFitScreen="true"
        app:verticalSplitterWidth="1dp"
        app:horizontalSplitterHeight="1dp"
        />
```
For more information read <a href="https://smartdevelopers-ir.github.io/smart-table-view/SmartTableDoc/" >  Smart Table Document </a>
