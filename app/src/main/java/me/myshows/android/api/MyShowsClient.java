package me.myshows.android.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashSet;
import java.util.Set;

import me.myshows.android.BuildConfig;
import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

/**
 * @author Whiplash
 * @date 14.06.2015
 */
public class MyShowsClient {

    private static final String TAG = MyShowsClient.class.getSimpleName();
    private static final String PREFERENCE_NAME = "myshows_api_preference";
    private static final String MYSHOWS_COOKIES = "myshows_cookies_token";
    private static final String API_URL = "http://api.myshows.ru";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE = "Cookie";

    private static MyShowsClient client;

    private final MyShowsApi api;
    private final Context context;

    private MyShowsClient(Context context) {
        this.context = context;
        this.api = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(request -> request.addHeader(COOKIE, TextUtils.join("; ", getCookies())))
                .build()
                .create(MyShowsApi.class);
    }

    public static MyShowsClient get(Context context) {
        if (client == null) {
            client = new MyShowsClient(context);
        }
        return client;
    }

    public void authentication(String login, String password, MyShowsCallback callback) {
        String md5Password = new String(Hex.encodeHex(DigestUtils.md5(password)));
        Callback<Response> responseCallback = new ResponseCallback() {
            @Override
            public void failure(RetrofitError error) {
                callback.getResponse(false);
            }

            @Override
            public void success(Response response) {
                Set<String> cookieValues = new HashSet<>();
                for (Header header : response.getHeaders()) {
                    if (SET_COOKIE.equals(header.getName())) {
                        cookieValues.add(parseSetCookie(header.getValue()));
                    }
                }
                saveCookies(cookieValues);
                callback.getResponse(true);
            }
        };
        api.login(login, md5Password, responseCallback);
    }

    public boolean isLogin() {
        Set<String> cookies = getCookies();
        return cookies != null && !cookies.isEmpty();
    }

    private String parseSetCookie(String setCookieValue) {
        return setCookieValue.substring(0, setCookieValue.indexOf(";"));
    }

    private void saveCookies(Set<String> cookieValues) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(MYSHOWS_COOKIES, cookieValues);
        editor.apply();
    }

    private Set<String> getCookies() {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getStringSet(MYSHOWS_COOKIES, new HashSet<>());
    }

    public interface MyShowsCallback {

        void getResponse(boolean response);
    }
}
