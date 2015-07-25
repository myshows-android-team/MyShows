package me.myshows.android.api.impl;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

import me.myshows.android.BuildConfig;
import me.myshows.android.api.ClientStorage;
import me.myshows.android.api.MyShowsApi;
import me.myshows.android.api.StorageMyShowsClient;
import me.myshows.android.dao.EntityPersistor;
import me.myshows.android.dao.entity.PersistentEntityConverter;
import me.myshows.android.entity.EpisodePreview;
import me.myshows.android.entity.Show;
import me.myshows.android.entity.User;
import me.myshows.android.entity.UserEpisode;
import me.myshows.android.entity.UserShow;
import me.myshows.android.serialization.JsonMatshaller;
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
public class MyShowsClientImpl extends StorageMyShowsClient {

    private static final String API_URL = "http://api.myshows.ru";
    private static final String COOKIE_DELIMITER = ";";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE = "Cookie";
    private static final PersistentEntityConverter converter = new PersistentEntityConverter(new JsonMatshaller());

    private static MyShowsClientImpl client;

    private final MyShowsApi api;
    private final EntityPersistor persistor;

    private Scheduler observerScheduler = Schedulers.immediate();

    private MyShowsClientImpl(Context context, ClientStorage storage) {
        super(storage);
        this.persistor = new EntityPersistor(context);
        this.api = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setConverter(new JacksonConverter())
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(request -> request.addHeader(COOKIE, TextUtils.join(COOKIE_DELIMITER, storage.getCookies())))
                .build()
                .create(MyShowsApi.class);
    }

    public static MyShowsClientImpl get(Context context, ClientStorage storage) {
        if (client == null) {
            client = new MyShowsClientImpl(context, storage);
        }
        return client;
    }

    public static MyShowsClientImpl get(Context context, ClientStorage storage, Scheduler observerScheduler) {
        if (client == null) {
            client = new MyShowsClientImpl(context, storage);
        }
        client.setObserverScheduler(observerScheduler);
        return client;
    }

    @Override
    public Observable<Boolean> authentication(Credentials credentials) {
        return Observable.create(subscriber -> api.login(credentials.getLogin(), credentials.getPasswordHash())
                .observeOn(observerScheduler)
                .subscribe(
                        response -> {
                            storage.putCredentials(credentials);
                            storage.putCookies(extractCookies(response));
                            subscriber.onNext(true);
                            subscriber.onCompleted();
                        },
                        e -> {
                            subscriber.onNext(false);
                            subscriber.onCompleted();
                        }));
    }

    @Override
    public Observable<User> profile() {
        return api.profile()
                .map(user -> persistor.persistEntity(user, converter::fromUser))
                .observeOn(observerScheduler);
    }

    @Override
    public Observable<UserShow> profileShows() {
        return api.profileShows()
                .concatMap(map -> Observable.from(persistor.persistEntityList(map.values(), converter::fromUserShow)))
                .observeOn(observerScheduler);
    }

    @Override
    public Observable<UserEpisode> profileEpisodesOfShow(int showId) {
        return api.profileEpisodesOfShow(showId)
                .concatMap(map -> Observable.from(persistor.persistEntityList(map.values(), converter::fromUserEpisode)))
                .observeOn(observerScheduler);
    }

    @Override
    public Observable<EpisodePreview> profileUnwatchedEpisodes() {
        return api.profileUnwatchedEpisodes()
                .concatMap(map -> Observable.from(persistor.persistEntityList(map.values(), converter::fromEpisodePreview)))
                .observeOn(observerScheduler);
    }

    @Override
    public Observable<EpisodePreview> profileNextEpisodes() {
        return api.profileNextEpisodes()
                .concatMap(map -> Observable.from(persistor.persistEntityList(map.values(), converter::fromEpisodePreview)))
                .observeOn(observerScheduler);
    }

    @Override
    public Observable<Show> showInformation(int showId) {
        return api.showInformation(showId)
                .map(show -> persistor.persistEntity(show, converter::fromShow))
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
