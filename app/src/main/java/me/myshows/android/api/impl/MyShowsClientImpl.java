package me.myshows.android.api.impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.myshows.android.BuildConfig;
import me.myshows.android.api.ClientStorage;
import me.myshows.android.api.MyShowsApi;
import me.myshows.android.api.StorageMyShowsClient;
import me.myshows.android.dao.RealmManager;
import me.myshows.android.dao.entity.PersistentEntityConverter;
import me.myshows.android.dao.entity.PersistentNextEpisode;
import me.myshows.android.dao.entity.PersistentShow;
import me.myshows.android.dao.entity.PersistentUnwatchedEpisode;
import me.myshows.android.dao.entity.PersistentUser;
import me.myshows.android.dao.entity.PersistentUserEpisode;
import me.myshows.android.dao.entity.PersistentUserShow;
import me.myshows.android.entity.NextEpisode;
import me.myshows.android.entity.Show;
import me.myshows.android.entity.UnwatchedEpisode;
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
    private final RealmManager manager;

    private Scheduler observerScheduler = Schedulers.immediate();

    private MyShowsClientImpl(Context context, ClientStorage storage) {
        super(storage);
        this.manager = new RealmManager(context);
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
        return Observable.<User>create(subscriber -> {
            User user = manager.getEntity(PersistentUser.class, converter::toUser,
                    Pair.create("login", storage.getCredentials().getLogin()));
            if (user != null) {
                subscriber.onNext(user);
            }
            api.profile()
                    .subscribe(
                            u -> subscriber.onNext(manager.persistEntity(u, converter::fromUser)),
                            e -> subscriber.onCompleted(),
                            subscriber::onCompleted
                    );
        }).observeOn(observerScheduler).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<UserShow>> profileShows() {
        return Observable.<List<UserShow>>create(subscriber -> {
            Class<PersistentUserShow> clazz = PersistentUserShow.class;
            List<UserShow> userShows = manager.getEntities(clazz, converter::toUserShow);
            if (userShows != null) {
                subscriber.onNext(userShows);
            }
            api.profileShows()
                    .subscribe(
                            us -> subscriber.onNext(manager.persistEntities(new ArrayList<>(us.values()), clazz, converter::fromUserShow)),
                            e -> subscriber.onCompleted(),
                            subscriber::onCompleted
                    );
        }).observeOn(observerScheduler).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<UserEpisode>> profileEpisodesOfShow(int showId) {
        return Observable.<List<UserEpisode>>create(subscriber -> {
            Class<PersistentUserEpisode> clazz = PersistentUserEpisode.class;
            List<UserEpisode> userEpisodes = manager.getEntities(clazz, converter::toUserEpisode,
                    Pair.create("id", showId));
            if (userEpisodes != null) {
                subscriber.onNext(userEpisodes);
            }
            api.profileEpisodesOfShow(showId)
                    .subscribe(
                            ue -> subscriber.onNext(manager.persistEntities(new ArrayList<>(ue.values()), clazz, converter::fromUserEpisode)),
                            e -> subscriber.onCompleted(),
                            subscriber::onCompleted
                    );
        }).observeOn(observerScheduler).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<UnwatchedEpisode>> profileUnwatchedEpisodes() {
        return Observable.<List<UnwatchedEpisode>>create(subscriber -> {
            Class<PersistentUnwatchedEpisode> clazz = PersistentUnwatchedEpisode.class;
            List<UnwatchedEpisode> unwatchedEpisodes = manager.getEntities(clazz, converter::toUnwatchedEpisodePreview);
            if (unwatchedEpisodes != null) {
                subscriber.onNext(unwatchedEpisodes);
            }
            api.profileUnwatchedEpisodes()
                    .subscribe(
                            uep -> subscriber.onNext(manager.persistEntities(new ArrayList<>(uep.values()), clazz, converter::fromUnwatchedEpisodePreview)),
                            e -> subscriber.onCompleted(),
                            subscriber::onCompleted
                    );
        }).observeOn(observerScheduler).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<NextEpisode>> profileNextEpisodes() {
        return Observable.<List<NextEpisode>>create(subscriber -> {
            Class<PersistentNextEpisode> clazz = PersistentNextEpisode.class;
            List<NextEpisode> unwatchedEpisodePreviews = manager.getEntities(clazz, converter::toNextEpisodePreview);
            if (unwatchedEpisodePreviews != null) {
                subscriber.onNext(unwatchedEpisodePreviews);
            }
            api.profileNextEpisodes()
                    .subscribe(
                            nep -> subscriber.onNext(manager.persistEntities(new ArrayList<>(nep.values()), clazz, converter::fromNextEpisodePreview)),
                            e -> subscriber.onCompleted(),
                            subscriber::onCompleted
                    );
        }).observeOn(observerScheduler).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Show> showInformation(int showId) {
        return Observable.<Show>create(subscriber -> {
            Show show = manager.getEntity(PersistentShow.class, converter::toShow,
                    Pair.create("id", showId));
            if (show != null) {
                subscriber.onNext(show);
            }
            api.showInformation(showId)
                    .subscribe(
                            s -> subscriber.onNext(manager.persistEntity(s, converter::fromShow)),
                            e -> subscriber.onCompleted(),
                            subscriber::onCompleted
                    );
        }).observeOn(observerScheduler).subscribeOn(Schedulers.io());
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
