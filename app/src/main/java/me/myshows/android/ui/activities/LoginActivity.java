package me.myshows.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import me.myshows.android.R;
import me.myshows.android.api.StorageMyShowsClient;
import me.myshows.android.api.impl.Credentials;
import me.myshows.android.api.impl.MyShowsClientImpl;
import me.myshows.android.api.impl.PreferenceStorage;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author Whiplash
 * @date 14.06.2015
 */
public class LoginActivity extends AppCompatActivity {

    private static final String REGISTER_URL = "http://myshows.me/";

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StorageMyShowsClient client = MyShowsClientImpl.get(new PreferenceStorage(getApplicationContext()),
                AndroidSchedulers.mainThread());

        if (client.hasCredentials()) {
            if (hasInternetConnection()) {
                processAuthenticationObserver(client.authentication());
            } else {
                changeActivity();
            }
        }

        TextView newAccount = (TextView) findViewById(R.id.new_account);
        setupNewAccountTextView(newAccount);

        findViewById(R.id.login_button).setOnClickListener(view -> {
            String login = ((EditText) findViewById(R.id.login)).getText().toString();
            String password = ((EditText) findViewById(R.id.password)).getText().toString();
            Credentials credentials = Credentials.make(login, password);
            processAuthenticationObserver(client.authentication(credentials));
        });
    }

    @Override
    protected void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

    private void setupNewAccountTextView(TextView view) {
        String register = getString(R.string.register);
        String newAccount = getString(R.string.new_account, register);
        int start = newAccount.indexOf(register);
        int end = start + register.length();
        SpannableString ss = new SpannableString(newAccount);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(REGISTER_URL));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.red_80_opacity));
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(ss, TextView.BufferType.SPANNABLE);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private boolean hasInternetConnection() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void processAuthenticationObserver(Observable<Boolean> observable) {
        subscription = observable.subscribe(result -> {
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
