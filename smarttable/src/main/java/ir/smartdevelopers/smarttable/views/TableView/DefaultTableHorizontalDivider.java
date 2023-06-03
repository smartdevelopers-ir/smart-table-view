package ir.smartdevelopers.smarttable.views.TableView;

import android.content.Context;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DefaultTableHorizontalDivider extends DividerItemDecoration {

    /**
     * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
     * {@link LinearLayoutManager}.
     *
     * @param context     Current context, it will be used to access resources.
     * @param orientation Divider orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    public DefaultTableHorizontalDivider(Context context, int orientation) {
        super(context, orientation);
    }
}
