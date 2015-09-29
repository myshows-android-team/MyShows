package me.myshows.android.ui.decorators;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by warrior on 17.09.15.
 */
public class BaseDecorator extends RecyclerView.ItemDecoration {

    protected boolean applyDecorator(View view, RecyclerView parent) {
        return true;
    }
}
