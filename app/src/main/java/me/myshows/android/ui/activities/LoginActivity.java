package me.myshows.android.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import me.myshows.android.MyShowsApplication;
import me.myshows.android.R;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.api.impl.Credentials;
import rx.Single;

/**
 * @author Whiplash
 * @date 14.06.2015
 */
public class LoginActivity extends RxAppCompatActivity {

    private static final String REGISTER_URL = "http://myshows.me/";
    private static final int ANIMATION_DURATION = 500;

    private MyShowsClient client;

    private View logo;
    private ViewGroup loginLayout;

    private boolean needAnimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }

        client = MyShowsApplication.getMyShowsClient(this);

        logo = findViewById(R.id.logo);
        loginLayout = (ViewGroup) findViewById(R.id.login_layout);

        TextView newAccount = (TextView) findViewById(R.id.new_account);
        setupNewAccountTextView(newAccount);

        findViewById(R.id.sign_in_button).setOnClickListener(view -> {
            String login = ((EditText) findViewById(R.id.login)).getText().toString();
            String password = ((EditText) findViewById(R.id.password)).getText().toString();
            Credentials credentials = Credentials.make(login, password);
            processAuthenticationObserver(client.authentication(credentials));
        });

        autoLoginAttempt();
    }

    private void autoLoginAttempt() {
        if (client.hasCredentials()) {
            if (hasInternetConnection()) {
                needAnimate = true;
                setEnabled(loginLayout, false);
                processAuthenticationObserver(client.autoAuthentication());
            } else {
                changeActivity();
            }
        } else {
            logo.setTranslationY(0);
            loginLayout.setAlpha(1);
        }
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
                ds.setColor(ContextCompat.getColor(LoginActivity.this, R.color.red_80_opacity));
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(ss, TextView.BufferType.SPANNABLE);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setEnabled(ViewGroup parent, boolean enabled) {
        parent.setEnabled(enabled);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                setEnabled((ViewGroup) child, enabled);
            } else {
                child.setEnabled(enabled);
            }
        }
    }

    private void animate() {
        ObjectAnimator logoAnimator = ObjectAnimator.ofFloat(logo, View.TRANSLATION_Y, 0);
        ObjectAnimator loginLayoutAnimator = ObjectAnimator.ofFloat(loginLayout, View.ALPHA, 1);
        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(logoAnimator, loginLayoutAnimator);
        animation.setDuration(ANIMATION_DURATION);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setEnabled(loginLayout, true);
                needAnimate = false;
            }
        });
        animation.start();
    }

    private boolean hasInternetConnection() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void processAuthenticationObserver(Single<Boolean> single) {
        // temporary workaround because RxLifecycle doesn't support Single
        single.toObservable()
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(result -> {
                    if (result) {
                        changeActivity();
                    } else {
                        if (needAnimate) {
                            animate();
                        } else {
                            Toast.makeText(this, R.string.incorrect_login_or_password, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void changeActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
