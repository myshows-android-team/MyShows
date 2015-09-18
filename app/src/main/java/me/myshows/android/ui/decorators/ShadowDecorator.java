package me.myshows.android.ui.decorators;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by warrior on 17.09.15.
 */
public class ShadowDecorator extends BaseDecorator {

    private final Drawable shadowDrawable;

    public ShadowDecorator(Drawable shadowDrawable) {
        this.shadowDrawable = shadowDrawable;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (applyDecorator(child, parent)) {
                int tx = (int) child.getTranslationX();
                int ty = (int) child.getTranslationY();

                int left = child.getLeft() + tx;
                int right = child.getRight() + tx;
                int top = child.getBottom() + ty;
                int bottom = child.getBottom() + ty + shadowDrawable.getMinimumHeight();

                shadowDrawable.setBounds(left, top, right, bottom);
                shadowDrawable.draw(c);
            }
        }
    }
}
