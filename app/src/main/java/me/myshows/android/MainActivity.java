package me.myshows.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.myshows.android.api.MyShowsClient;
import me.myshows.android.entities.User;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author Whiplash
 * @date 18.06.2015
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyShowsClient.get(getApplicationContext()).profile(new Callback<User>() {
            @Override
            public void success(User user, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
