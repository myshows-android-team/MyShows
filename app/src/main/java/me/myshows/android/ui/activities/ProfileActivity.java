package me.myshows.android.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import me.myshows.android.MyShowsApplication;
import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.model.User;
import me.myshows.android.ui.fragments.AvatarProfileFragment;
import me.myshows.android.ui.fragments.ProfileHeaderFragment;
import me.myshows.android.ui.fragments.StatisticProfileFragment;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by warrior on 20.09.15.
 */
public class ProfileActivity extends HomeActivity {

    private static final float SEMI_TRANSPARENT_ALPHA = 127.5F;

    private ViewPager viewPager;
    private CircleIndicator indicator;
    private GradientDrawable gradient;

    private HeaderPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar(toolbar);

        View backgroundGradient = findViewById(R.id.background_gradient);
        gradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.TRANSPARENT, Color.argb((int) SEMI_TRANSPARENT_ALPHA, 0, 0, 0)});
        backgroundGradient.setBackground(gradient);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        setupViewPager();

        ImageView headerImage = (ImageView) findViewById(R.id.header_image);
        setHeaderBackground(headerImage);

        loadData();
    }

    @Override
    protected void onPostSetupActionBar(@NonNull ActionBar actionBar) {
        // TODO: 16.10.15 set correct title
        actionBar.setTitle("");
    }

    private void setupViewPager() {
        adapter = new HeaderPageAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int alpha = (int) (SEMI_TRANSPARENT_ALPHA * (1 + position + positionOffset));
                int bottomColor = Color.argb(alpha, 0, 0, 0);
                gradient.setColors(new int[]{Color.TRANSPARENT, bottomColor});
            }
        });
        indicator.setViewPager(viewPager);
    }

    private void setHeaderBackground(ImageView headerImage) {
        // TODO: we need use background which user will select
        Glide.with(this)
                .load("http://media.myshows.me/shows/normal/d/da/da3e7aee7483129e27208bd8e36c0b64.jpg")
                .crossFade()
                .into(headerImage);
    }

    private void loadData() {
        MyShowsClient client = MyShowsApplication.getMyShowsClient(this);
        client.profile()
                .compose(bindToLifecycle())
                .subscribe(adapter::setUser);
    }

    private static class HeaderPageAdapter extends FragmentStatePagerAdapter {

        private User user;

        public HeaderPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return AvatarProfileFragment.newInstance(user);
            } else {
                return StatisticProfileFragment.newInstance(user);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public int getItemPosition(Object object) {
            ProfileHeaderFragment fragment = (ProfileHeaderFragment) object;
            if (user != null) {
                fragment.setUser(user);
            }
            return super.getItemPosition(object);
        }

        public void setUser(User user) {
            this.user = user;
            notifyDataSetChanged();
        }
    }
}
