package me.myshows.android.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.myshows.android.BuildConfig;
import me.myshows.android.entities.EpisodePreview;
import me.myshows.android.entities.EpisodeRating;
import me.myshows.android.entities.User;
import me.myshows.android.entities.UserShow;
import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

/**
 * @author Whiplash
 * @date 14.06.2015
 */
public class MyShowsClient {

    private static final String TAG = MyShowsClient.class.getSimpleName();
    private static final String PREFERENCE_NAME = "my_shows_api_preference";
    private static final String MY_SHOWS_COOKIES = "my_shows_cookies_token";
    private static final String API_URL = "http://api.myshows.ru";
    private static final String COOKIE_DELIMITER = ";";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE = "Cookie";

    private static MyShowsClient client;

    private final MyShowsApi api;
    private final SharedPreferences preferences;

    private MyShowsClient(Context context) {
        this.preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.api = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setConverter(new JacksonConverter())
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(request -> request.addHeader(COOKIE, TextUtils.join(COOKIE_DELIMITER, getCookies())))
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

    public void profile(Callback<User> callback) {
        api.profile(callback);
    }

    public void profileShows(Callback<Map<String, UserShow>> callback) {
        api.profileShows(callback);
    }

    public void profileEpisodesOfShow(int showId, Callback<Map<String, EpisodeRating>> callback) {
        api.profileEpisodesOfShow(showId, callback);
    }

    public void profileUnwatchedEpisodes(Callback<Map<String, EpisodePreview>> callback) {
        api.profileUnwatchedEpisodes(callback);
    }

    public void profileNextEpisodes(Callback<Map<String, EpisodePreview>> callback) {
        api.profileNextEpisodes(callback);
    }

    public boolean isLogin() {
        return !getCookies().isEmpty();
    }

    private String parseSetCookie(String setCookieValue) {
        return setCookieValue.substring(0, setCookieValue.indexOf(COOKIE_DELIMITER));
    }

    private void saveCookies(Set<String> cookieValues) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(MY_SHOWS_COOKIES, cookieValues);
        editor.apply();
    }

    @NonNull
    private Set<String> getCookies() {
        return preferences.getStringSet(MY_SHOWS_COOKIES, new HashSet<>());
    }

    public interface MyShowsCallback {

        void getResponse(boolean response);
    }
}
