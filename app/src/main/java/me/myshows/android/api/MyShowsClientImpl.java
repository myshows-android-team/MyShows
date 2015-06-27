package me.myshows.android.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashSet;
import java.util.Set;

import me.myshows.android.BuildConfig;
import me.myshows.android.entities.EpisodePreview;
import me.myshows.android.entities.EpisodeRating;
import me.myshows.android.entities.Show;
import me.myshows.android.entities.User;
import me.myshows.android.entities.UserShow;
import retrofit.RestAdapter;
import retrofit.client.Header;
import retrofit.converter.JacksonConverter;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * @author Whiplash
 * @date 14.06.2015
 */
public class MyShowsClientImpl implements MyShowsClient {

    private static final String TAG = MyShowsClientImpl.class.getSimpleName();
    private static final String PREFERENCE_NAME = "my_shows_api_preference";
    private static final String MY_SHOWS_COOKIES = "my_shows_cookies_token";
    private static final String API_URL = "http://api.myshows.ru";
    private static final String COOKIE_DELIMITER = ";";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE = "Cookie";

    private static MyShowsClientImpl client;

    private final MyShowsApi api;
    private final SharedPreferences preferences;

    private Scheduler observerScheduler = Schedulers.immediate();

    private MyShowsClientImpl(Context context) {
        this.preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.api = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setConverter(new JacksonConverter())
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(request -> request.addHeader(COOKIE, TextUtils.join(COOKIE_DELIMITER, getCookies())))
                .build()
                .create(MyShowsApi.class);
    }

    public static MyShowsClientImpl get(Context context) {
        if (client == null) {
            client = new MyShowsClientImpl(context);
        }
        return client;
    }

    public static MyShowsClientImpl get(Context context, Scheduler observerScheduler) {
        if (client == null) {
            client = new MyShowsClientImpl(context);
        }
        client.setObserverScheduler(observerScheduler);
        return client;
    }

    @Override
    public void authentication(String login, String password, MyShowsClient.MyShowsCallback callback) {
        String md5Password = new String(Hex.encodeHex(DigestUtils.md5(password)));
        api.login(login, md5Password)
                .observeOn(observerScheduler)
                .subscribe(
                        response -> {
                            Set<String> cookieValues = new HashSet<>();
                            for (Header header : response.getHeaders()) {
                                if (SET_COOKIE.equals(header.getName())) {
                                    cookieValues.add(parseSetCookie(header.getValue()));
                                }
                            }
                            saveCookies(cookieValues);
                            callback.getResponse(true);
                        },
                        e -> {
                            e.printStackTrace();
                            callback.getResponse(false);
                        });
    }

    @Override
    public boolean isLogin() {
        return !getCookies().isEmpty();
    }

    @Override
    public Observable<User> profile() {
        return api.profile()
                .observeOn(observerScheduler);
    }

    @Override
    public Observable<UserShow> profileShows() {
        return api.profileShows()
                .concatMap(m -> Observable.from(m.values()))
                .observeOn(observerScheduler);
    }

    @Override
    public Observable<EpisodeRating> profileEpisodesOfShow(int showId) {
        return api.profileEpisodesOfShow(showId)
                .concatMap(m -> Observable.from(m.values()))
                .observeOn(observerScheduler);
    }

    @Override
    public Observable<EpisodePreview> profileUnwatchedEpisodes() {
        return api.profileUnwatchedEpisodes()
                .concatMap(m -> Observable.from(m.values()))
                .observeOn(observerScheduler);
    }

    @Override
    public Observable<EpisodePreview> profileNextEpisodes() {
        return api.profileNextEpisodes()
                .concatMap(m -> Observable.from(m.values()))
                .observeOn(observerScheduler);
    }

    @Override
    public Observable<Show> showInformation(int showId) {
        return api.showInformation(showId)
                .observeOn(observerScheduler);
    }

    @Override
    public void setObserverScheduler(Scheduler scheduler) {
        this.observerScheduler = scheduler;
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
}
