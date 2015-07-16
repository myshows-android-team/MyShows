package me.myshows.android.api.impl;

import android.content.Context;
import android.text.TextUtils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

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
    public Observable<Boolean> authentication(String login, String password) {
        String md5Password = new String(Hex.encodeHex(DigestUtils.md5(password)));
        return getAuthenticationObserver(login, md5Password);
    }

    @Override
    public Observable<Boolean> authentication() {
        storage.setCookies(new HashSet<>());
        return getAuthenticationObserver(storage.getLogin(), storage.getPasswordHash());
    }

    private Observable<Boolean> getAuthenticationObserver(String login, String md5Password) {
        return api.login(login, md5Password)
                .observeOn(observerScheduler)
                .map(response -> {
                    storage.setLogin(login);
                    storage.setPasswordHash(md5Password);
                    storage.setCookies(extractCookies(response));
                    return true;
                });
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
    public boolean hasCredential() {
        return storage.containsCredential();
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