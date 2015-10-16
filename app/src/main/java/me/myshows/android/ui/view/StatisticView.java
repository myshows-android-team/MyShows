package me.myshows.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.myshows.android.R;

/**
 * Created by warrior on 04.10.15.
 */
public class StatisticView extends LinearLayout {

    private final int captionResId;

    private TextView captionView;
    private TextView valueView;

    public StatisticView(Context context) {
        this(context, null);
    }

    public StatisticView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatisticView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.statistic_value, this);
        View.inflate(context, R.layout.statistic_caption, this);
        setOrientation(VERTICAL);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StatisticView);
        captionResId = a.getResourceId(R.styleable.StatisticView_caption, -1);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        valueView = (TextView) findViewById(R.id.value);
        captionView = (TextView) findViewById(R.id.caption);
    }

    public void setValue(int value) {
        if (captionResId >= 0) {
            captionView.setText(getResources().getQuantityString(captionResId, value));
        }
        valueView.setText(String.valueOf(value));
    }
}
