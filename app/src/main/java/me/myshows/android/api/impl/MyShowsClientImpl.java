package me.myshows.android.api.impl;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

import me.myshows.android.BuildConfig;
import me.myshows.android.api.ClientStorage;
import me.myshows.android.api.MyShowsApi;
import me.myshows.android.api.MyShowsClient;
import me.myshows.android.entities.EpisodePreview;
import me.myshows.android.entities.EpisodeRating;
import me.myshows.android.entities.Show;
import me.myshows.android.entities.User;
import me.myshows.android.entities.UserShow;
import retrofit.RestAdapter;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * @author Whiplash
 * @date 14.06.2015
 */
public class MyShowsClientImpl implements MyShowsClient {

    private static final String API_URL = "http://api.myshows.ru";
    private static final String COOKIE_DELIMITER = ";";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE = "Cookie";

    private static MyShowsClientImpl client;

    private final MyShowsApi api;
    private final ClientStorage storage;

    private Scheduler observerScheduler = Schedulers.immediate();

    private MyShowsClientImpl(Context context) {
        this.storage = new PreferenceStorage(context);
        this.api = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setConverter(new JacksonConverter())
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(request -> request.addHeader(COOKIE, TextUtils.join(COOKIE_DELIMITER, storage.getCookies())))
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
    public Observable<Boolean> authentication(Credentials credentials) {
        return api.login(credentials.getLogin(), credentials.getPasswordHash())
                .observeOn(observerScheduler)
                .map(response -> {
                    storage.putCredentials(credentials);
                    storage.putCookies(extractCookies(response));
                    return true;
                });
    }

    @Override
    public Observable<Boolean> authentication() {
        storage.putCookies(new HashSet<>()); // reset cookie otherwise API returns 401
        return authentication(storage.getCredentials());
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

    @Override
    public boolean hasCredentials() {
        return storage.getCredentials() != null;
    }

    private Set<String> extractCookies(Response response) {
        Set<String> cookieValues = new HashSet<>();
        for (Header header : response.getHeaders()) {
            if (SET_COOKIE.equals(header.getName())) {
                cookieValues.add(parseSetCookie(header.getValue()));
            }
        }
        return cookieValues;
    }

    private String parseSetCookie(String setCookieValue) {
        return setCookieValue.substring(0, setCookieValue.indexOf(COOKIE_DELIMITER));
    }
}
