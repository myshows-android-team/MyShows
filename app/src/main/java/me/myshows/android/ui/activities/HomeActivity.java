package me.myshows.android.ui.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

/**
 * Created by warrior on 04.10.15.
 */
public abstract class HomeActivity extends RxAppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setupActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            onPostSetupActionBar(actionBar);
        }
    }

    protected void onPostSetupActionBar(@NonNull ActionBar actionBar) {
    }
}
