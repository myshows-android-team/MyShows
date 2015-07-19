package me.myshows.android.ui.activities;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.api.impl.PreferenceStorage;
import me.myshows.android.ui.fragments.CalendarFragment;
import me.myshows.android.ui.fragments.FavoritesFragment;
import me.myshows.android.ui.fragments.FriendsFragment;
import me.myshows.android.ui.fragments.MyShowsFragment;
import me.myshows.android.ui.fragments.ProfileFragment;
import me.myshows.android.ui.fragments.RatingsFragment;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author Whiplash
 * @date 18.06.2015
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    
    private static final SparseIntArray MENU_ITEM_ID_TO_TITLE = new SparseIntArray();
    private static final SparseArray<Class<? extends Fragment>> MENU_ITEM_ID_TO_FRAGMENT = new SparseArray<>();
    
    static {
        MENU_ITEM_ID_TO_TITLE.put(R.id.nav_my_series, R.string.my_series);
        MENU_ITEM_ID_TO_TITLE.put(R.id.nav_calendar, R.string.calendar);
        MENU_ITEM_ID_TO_TITLE.put(R.id.nav_profile, R.string.profile);
        MENU_ITEM_ID_TO_TITLE.put(R.id.nav_friends, R.string.friends);
        MENU_ITEM_ID_TO_TITLE.put(R.id.nav_favorites, R.string.favorites);
        MENU_ITEM_ID_TO_TITLE.put(R.id.nav_ratings, R.string.ratings);

        MENU_ITEM_ID_TO_FRAGMENT.put(R.id.nav_my_series, MyShowsFragment.class);
        MENU_ITEM_ID_TO_FRAGMENT.put(R.id.nav_calendar, CalendarFragment.class);
        MENU_ITEM_ID_TO_FRAGMENT.put(R.id.nav_profile, ProfileFragment.class);
        MENU_ITEM_ID_TO_FRAGMENT.put(R.id.nav_friends, FriendsFragment.class);
        MENU_ITEM_ID_TO_FRAGMENT.put(R.id.nav_favorites, FavoritesFragment.class);
        MENU_ITEM_ID_TO_FRAGMENT.put(R.id.nav_ratings, RatingsFragment.class);
    }

    private FragmentManager fragmentManager;

    private DrawerLayout drawerLayout;
    private ImageView avatar;
    private TextView username;

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.content, new MyShowsFragment(), MyShowsFragment.class.getSimpleName())
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        setupNavigationDrawer(navigationView);

        avatar = (ImageView) findViewById(R.id.nav_avatar);
        username = (TextView) findViewById(R.id.nav_username);

        loadData();
    }

    @Override
    protected void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
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

    private void setupActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupNavigationDrawer(NavigationView navigationView) {
        navigationView.getMenu()
                .findItem(R.id.nav_my_series)
                .setChecked(true);
        setActionBarTitle(R.string.my_series);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    Class<? extends Fragment> fragmentClass = MENU_ITEM_ID_TO_FRAGMENT.get(menuItem.getItemId());
                    if (fragmentClass != null && !menuItem.isChecked()) {
                        int titleRes = MENU_ITEM_ID_TO_TITLE.get(menuItem.getItemId());
                        setActionBarTitle(titleRes);
                        Fragment fragment = getFragment(fragmentClass);
                        fragmentManager.beginTransaction()
                                .replace(R.id.content, fragment, fragmentClass.getSimpleName())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                        menuItem.setChecked(true);
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
        }
    }

    private void loadData() {
        MyShowsClient client = MyShowsClientImpl.get(new PreferenceStorage(getApplicationContext()),
                AndroidSchedulers.mainThread());
        subscription = client.profile()
                .subscribe(user -> {
                    username.setText(user.getLogin());
                    Glide.with(this)
                            .load(user.getAvatarUrl())
                            .into(avatar);
                });
    }
}
