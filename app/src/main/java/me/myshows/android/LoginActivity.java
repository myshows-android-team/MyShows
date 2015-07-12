package me.myshows.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import me.myshows.android.api.MyShowsClient;
import me.myshows.android.api.MyShowsClientImpl;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author Whiplash
 * @date 14.06.2015
 */
public class LoginActivity extends AppCompatActivity {

    private MyShowsClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        client = MyShowsClientImpl.get(getApplicationContext(), AndroidSchedulers.mainThread());

        if (client.hasCredential()) {
            processAuthenticationObserver(client.authentication());
        }

        findViewById(R.id.loginButton).setOnClickListener(view -> {
            String login = ((EditText) findViewById(R.id.login)).getText().toString();
            String password = ((EditText) findViewById(R.id.password)).getText().toString();
            processAuthenticationObserver(client.authentication(login, password));
        });
    }

    private void processAuthenticationObserver(Observable<Boolean> observable) {
        observable.subscribe(
                state -> userHasLogin(),
                e -> Toast.makeText(this, R.string.incorrect_login_or_password, Toast.LENGTH_SHORT).show()
        );
    }

    private void userHasLogin() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
