package me.myshows.android;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.myshows.android.api.MyShowsClient;
import me.myshows.android.api.MyShowsClientImpl;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author Whiplash
 * @date 18.06.2015
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView avatar;
    private TextView username;

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.my_series);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        MyShowsClient client = MyShowsClientImpl.get(getApplicationContext(), AndroidSchedulers.mainThread());

        username = (TextView) findViewById(R.id.nav_username);
        avatar = (ImageView) findViewById(R.id.nav_avatar);

        subscription = client.profile()
                .subscribe(user -> {
                    username.setText(user.getLogin());
                    Glide.with(this)
                            .load(user.getAvatarUrl())
                            .into(avatar);
                });
    }

    @Override
    protected void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }
}
