package me.myshows.android.ui.activities;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.ui.fragments.CalendarFragment;
import me.myshows.android.ui.fragments.FavoritesFragment;
import me.myshows.android.ui.fragments.FriendsFragment;
import me.myshows.android.ui.fragments.MyShowsFragment;
import me.myshows.android.ui.fragments.ProfileFragment;
import me.myshows.android.ui.fragments.RatingsFragment;

/**
 * @author Whiplash
 * @date 18.06.2015
 */
public class MainActivity extends RxAppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String MENU_ITEM_ID = "menuItemId";

    private static final SparseArray<FragmentInfo> MENU_ITEM_ID_TO_FRAGMENT_INFO = new SparseArray<>();

    static {
        MENU_ITEM_ID_TO_FRAGMENT_INFO.put(R.id.nav_my_series, FragmentInfo.make(MyShowsFragment.class, R.string.my_series));
        MENU_ITEM_ID_TO_FRAGMENT_INFO.put(R.id.nav_calendar, FragmentInfo.make(CalendarFragment.class, R.string.calendar));
        MENU_ITEM_ID_TO_FRAGMENT_INFO.put(R.id.nav_profile, FragmentInfo.make(ProfileFragment.class, R.string.profile));
        MENU_ITEM_ID_TO_FRAGMENT_INFO.put(R.id.nav_friends, FragmentInfo.make(FriendsFragment.class, R.string.friends));
        MENU_ITEM_ID_TO_FRAGMENT_INFO.put(R.id.nav_favorites, FragmentInfo.make(FavoritesFragment.class, R.string.favorites));
        MENU_ITEM_ID_TO_FRAGMENT_INFO.put(R.id.nav_ratings, FragmentInfo.make(RatingsFragment.class, R.string.ratings));
    }

    private FragmentManager fragmentManager;

    private DrawerLayout drawerLayout;
    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appbarLayout;
    private ImageView avatar;
    private ImageView headerBackground;
    private TextView username;

    private int currentItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.content, new MyShowsFragment(), MyShowsFragment.class.getSimpleName())
                    .commit();
            currentItemId = R.id.nav_my_series;
        } else {
            currentItemId = savedInstanceState.getInt(MENU_ITEM_ID);
        }

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        appbarLayout = (AppBarLayout) findViewById(R.id.appbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        setupNavigationDrawer(navigationView);

        username = (TextView) findViewById(R.id.nav_username);
        headerBackground = (ImageView) findViewById(R.id.nav_background);
        avatar = (ImageView) findViewById(R.id.nav_avatar);

        loadData();
        setHeaderBackground();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MENU_ITEM_ID, currentItemId);
        super.onSaveInstanceState(outState);
    }

    private void setupActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(MENU_ITEM_ID_TO_FRAGMENT_INFO.get(currentItemId).titleId);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupNavigationDrawer(NavigationView navigationView) {
        navigationView.getMenu()
                .findItem(currentItemId)
                .setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    FragmentInfo info = MENU_ITEM_ID_TO_FRAGMENT_INFO.get(menuItem.getItemId());
                    if (info != null && currentItemId != menuItem.getItemId()) {
                        currentItemId = menuItem.getItemId();
                        setActionBarTitle(info.titleId);
                        Fragment oldFragment = fragmentManager.findFragmentById(R.id.content);
                        Fragment newFragment = getFragment(info.fragmentClass);
                        fragmentManager.beginTransaction()
                                .detach(oldFragment)
                                .replace(R.id.content, newFragment, info.fragmentClass.getSimpleName())
                                .attach(newFragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                    }
                    drawerLayout.closeDrawers();
                    return true;
                });
    }

    private Fragment getFragment(Class<? extends Fragment> clazz) {
        Fragment fragment = fragmentManager.findFragmentByTag(clazz.getSimpleName());
        if (fragment == null) {
            fragment = Fragment.instantiate(this, clazz.getCanonicalName());
        }
        return fragment;
    }

    private void setActionBarTitle(@StringRes int resId) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(resId);
            resetAppBarOffset();
        }
    }

    private void resetAppBarOffset() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.setTopAndBottomOffset(0);
            behavior.onNestedPreScroll(coordinatorLayout, appbarLayout, null, 0, -1, new int[2]);
        }
    }

    private void loadData() {
        MyShowsClient client = MyShowsClientImpl.getInstance();
        client.profile()
                .compose(bindToLifecycle())
                .subscribe(user -> {
                    username.setText(user.getLogin());
                    Glide.with(this)
                            .load(user.getAvatarUrl())
                            .into(avatar);
                });
    }

    private void setHeaderBackground() {
        // TODO: we need use background which user will select
        Glide.with(this)
                .load("http://media.myshows.me/shows/normal/d/da/da3e7aee7483129e27208bd8e36c0b64.jpg")
                .crossFade()
                .into(headerBackground);
    }

    private static class FragmentInfo {
        public final Class<? extends Fragment> fragmentClass;
        @StringRes public final int titleId;

        public FragmentInfo(Class<? extends Fragment> fragmentClass, @StringRes int titleId) {
            this.fragmentClass = fragmentClass;
            this.titleId = titleId;
        }

        public static FragmentInfo make(Class<? extends Fragment> clazz, @StringRes int titleId) {
            return new FragmentInfo(clazz, titleId);
        }
    }
}
