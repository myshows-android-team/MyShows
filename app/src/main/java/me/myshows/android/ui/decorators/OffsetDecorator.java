package me.myshows.android.ui.decorators;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by warrior on 17.09.15.
 */
public class OffsetDecorator extends BaseDecorator {

    public static final int LEFT_OFFSET = 1 << 1;
    public static final int TOP_OFFSET = 1 << 2;
    public static final int RIGHT_OFFSET = 1 << 3;
    public static final int BOTTOM_OFFSET = 1 << 4;

    private final int leftOffset;
    private final int topOffset;
    private final int rightOffset;
    private final int bottomOffset;

    public OffsetDecorator(int offset, int flags) {
        this.leftOffset = (flags & LEFT_OFFSET) != 0 ? offset : 0;
        this.topOffset = (flags & TOP_OFFSET) != 0 ? offset : 0;
        this.rightOffset = (flags & RIGHT_OFFSET) != 0 ? offset : 0;
        this.bottomOffset = (flags & BOTTOM_OFFSET) != 0 ? offset : 0;
    }

    public OffsetDecorator(int leftOffset, int topOffset, int rightOffset, int bottomOffset) {
        this.leftOffset = leftOffset;
        this.topOffset = topOffset;
        this.rightOffset = rightOffset;
        this.bottomOffset = bottomOffset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (applyDecorator(view, parent)) {
            outRect.set(leftOffset, topOffset, rightOffset, bottomOffset);
        }
    }
}
