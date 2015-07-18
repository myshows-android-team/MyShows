package me.myshows.android;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import me.myshows.android.api.StorageMyShowsClient;
import me.myshows.android.api.impl.Credentials;
import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.api.impl.PreferenceStorage;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author Whiplash
 * @date 14.06.2015
 */
public class LoginActivity extends AppCompatActivity {

    private StorageMyShowsClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        client = MyShowsClientImpl.get(new PreferenceStorage(getApplicationContext()),
                AndroidSchedulers.mainThread());

        if (client.hasCredentials()) {
            if (hasInternetConnection()) {
                processAuthenticationObserver(client.authentication());
            } else {
                changeActivity();
            }
        }

        findViewById(R.id.loginButton).setOnClickListener(view -> {
            String login = ((EditText) findViewById(R.id.login)).getText().toString();
            String password = ((EditText) findViewById(R.id.password)).getText().toString();
            Credentials credentials = Credentials.make(login, password);
            processAuthenticationObserver(client.authentication(credentials));
        });
    }

    private boolean hasInternetConnection() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void processAuthenticationObserver(Observable<Boolean> observable) {
        observable.subscribe(result -> {
            if (result) {
                changeActivity();
            } else {
                Toast.makeText(this, R.string.incorrect_login_or_password, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
