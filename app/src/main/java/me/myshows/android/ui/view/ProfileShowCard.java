package me.myshows.android.ui.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

/**
 * Created by warrior on 30.10.15.
 */
public class ProfileShowCard extends CardView {

    private static final double HEIGHT_RATIO = 9D / 16;

    public ProfileShowCard(Context context) {
        super(context);
    }

    public ProfileShowCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfileShowCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (HEIGHT_RATIO * width + 0.5);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }
}
