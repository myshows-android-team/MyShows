package me.myshows.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.myshows.android.api.MyShowsClient;

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
        MyShowsClient.get(getApplicationContext()).showInformation(5713);
    }
}
