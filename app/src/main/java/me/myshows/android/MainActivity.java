package me.myshows.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Map;

import me.myshows.android.api.MyShowsClient;
import me.myshows.android.entities.Episode;
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

        // test
        MyShowsClient.get(getApplicationContext()).profileEpisodesOfShow(5317, new Callback<Map<String, Episode>>() {
            @Override
            public void success(Map<String, Episode> stringEpisodeMap, Response response) {
                stringEpisodeMap.size();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
}
