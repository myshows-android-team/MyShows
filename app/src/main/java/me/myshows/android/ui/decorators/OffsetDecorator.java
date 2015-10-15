package me.myshows.android.ui.decorators;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by warrior on 17.09.15.
 */
public class OffsetDecorator extends BaseDecorator {

    private final int offset;

    public OffsetDecorator(int offset) {
        this.offset = offset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (applyDecorator(view, parent)) {
            outRect.set(0, offset, 0, 0);
        }
    }
}
