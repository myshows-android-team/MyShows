package me.myshows.android.api.impl;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.myshows.android.BuildConfig;
import me.myshows.android.api.ClientStorage;
import me.myshows.android.api.MyShowsApi;
import me.myshows.android.api.StorageMyShowsClient;
import me.myshows.android.model.NextEpisode;
import me.myshows.android.model.RatingShow;
import me.myshows.android.model.Show;
import me.myshows.android.model.UnwatchedEpisode;
import me.myshows.android.model.User;
import me.myshows.android.model.UserEpisode;
import me.myshows.android.model.UserShow;
import me.myshows.android.model.persistent.PersistentNextEpisode;
import me.myshows.android.model.persistent.PersistentRatingShow;
import me.myshows.android.model.persistent.PersistentShow;
import me.myshows.android.model.persistent.PersistentUnwatchedEpisode;
import me.myshows.android.model.persistent.PersistentUser;
import me.myshows.android.model.persistent.PersistentUserEpisode;
import me.myshows.android.model.persistent.PersistentUserShow;
import me.myshows.android.model.persistent.dao.PersistentEntityConverter;
import me.myshows.android.model.persistent.dao.Predicate;
import me.myshows.android.model.persistent.dao.RealmManager;
import me.myshows.android.model.serialization.JsonMarshaller;
import me.myshows.android.utils.Integers;
import retrofit.RestAdapter;
import retrofit.client.Header;
import retrofit.client.OkClient;
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
    private static final PersistentEntityConverter converter = new PersistentEntityConverter(new JsonMarshaller());

    private static MyShowsClientImpl client;

    private final MyShowsApi api;
    private final RealmManager manager;
    private final Scheduler observerScheduler;

    private MyShowsClientImpl(ClientStorage storage, Scheduler observerScheduler) {
        super(storage);
        this.manager = new RealmManager();
        this.api = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setClient(new OkClient())
                .setConverter(new JacksonConverter())
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(request -> request.addHeader(COOKIE, TextUtils.join(COOKIE_DELIMITER, storage.getCookies())))
                .build()
                .create(MyShowsApi.class);
        this.observerScheduler = observerScheduler;
    }

    public static void init(ClientStorage storage, Scheduler observerScheduler) {
        client = new MyShowsClientImpl(storage, observerScheduler);
    }

    public static MyShowsClientImpl getInstance() {
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
            User user = manager.selectEntity(PersistentUser.class, converter::toUser,
                    new Predicate("login", storage.getCredentials().getLogin()));
            if (user != null) {
                subscriber.onNext(user);
            }
            api.profile()
                    .subscribe(
                            u -> subscriber.onNext(manager.upsertEntity(u, converter::fromUser)),
                            e -> subscriber.onCompleted(),
                            subscriber::onCompleted
                    );
        }).observeOn(observerScheduler).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<UserShow>> profileShows() {
        return Observable.<List<UserShow>>create(subscriber -> {
            Class<PersistentUserShow> clazz = PersistentUserShow.class;
            List<UserShow> userShows = manager.selectEntities(clazz, converter::toUserShow);
            if (userShows != null) {
                subscriber.onNext(userShows);
            }
            api.profileShows()
                    .subscribe(
                            us -> subscriber.onNext(manager.truncateAndInsertEntities(new ArrayList<>(us.values()), clazz, converter::fromUserShow)),
                            e -> subscriber.onCompleted(),
                            subscriber::onCompleted
                    );
        }).observeOn(observerScheduler).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<UserEpisode>> profileEpisodesOfShow(int showId) {
        return Observable.<List<UserEpisode>>create(subscriber -> {
            Class<PersistentUserEpisode> clazz = PersistentUserEpisode.class;
            List<UserEpisode> userEpisodes = manager.selectEntities(clazz, converter::toUserEpisode,
                    new Predicate("id", showId));
            if (userEpisodes != null) {
                subscriber.onNext(userEpisodes);
            }
            api.profileEpisodesOfShow(showId)
                    .subscribe(
                            ue -> subscriber.onNext(manager.truncateAndInsertEntities(new ArrayList<>(ue.values()), clazz, converter::fromUserEpisode)),
                            e -> subscriber.onCompleted(),
                            subscriber::onCompleted
                    );
        }).observeOn(observerScheduler).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<UnwatchedEpisode>> profileUnwatchedEpisodes() {
        return Observable.<List<UnwatchedEpisode>>create(subscriber -> {
            Class<PersistentUnwatchedEpisode> clazz = PersistentUnwatchedEpisode.class;
            List<UnwatchedEpisode> unwatchedEpisodes = manager.selectEntities(clazz, converter::toUnwatchedEpisode);
            if (unwatchedEpisodes != null) {
                subscriber.onNext(unwatchedEpisodes);
            }
            api.profileUnwatchedEpisodes()
                    .subscribe(
                            uep -> subscriber.onNext(manager.truncateAndInsertEntities(new ArrayList<>(uep.values()), clazz, converter::fromUnwatchedEpisode)),
                            e -> subscriber.onCompleted(),
                            subscriber::onCompleted
                    );
        }).observeOn(observerScheduler).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<NextEpisode>> profileNextEpisodes() {
        return Observable.<List<NextEpisode>>create(subscriber -> {
            Class<PersistentNextEpisode> clazz = PersistentNextEpisode.class;
            List<NextEpisode> unwatchedEpisodePreviews = manager.selectEntities(clazz, converter::toNextEpisode);
            if (unwatchedEpisodePreviews != null) {
                subscriber.onNext(unwatchedEpisodePreviews);
            }
            api.profileNextEpisodes()
                    .subscribe(
                            nep -> subscriber.onNext(manager.truncateAndInsertEntities(new ArrayList<>(nep.values()), clazz, converter::fromNextEpisode)),
                            e -> subscriber.onCompleted(),
                            subscriber::onCompleted
                    );
        }).observeOn(observerScheduler).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Show> showInformation(int showId) {
        return Observable.<Show>create(subscriber -> {
            Show show = manager.selectEntity(PersistentShow.class, converter::toShow,
                    new Predicate("id", showId));
            if (show != null) {
                subscriber.onNext(show);
            }
            api.showInformation(showId)
                    .subscribe(
                            s -> subscriber.onNext(manager.upsertEntity(s, converter::fromShow)),
                            e -> subscriber.onCompleted(),
                            subscriber::onCompleted
                    );
        }).observeOn(observerScheduler).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<RatingShow>> ratingShows() {
        return Observable.<List<RatingShow>>create(subscriber -> {
            List<RatingShow> ratingShows = manager.selectSortedEntities(PersistentRatingShow.class, converter::toRatingShow, "place", true);
            if (ratingShows != null) {
                subscriber.onNext(ratingShows);
            }
            api.ratingShows()
                    .map(shows -> {
                        Collections.sort(shows, (s1, s2) -> Integers.compare(s1.getPlace(), s2.getPlace()));
                        return shows;
                    })
                    .subscribe(
                            shows -> subscriber.onNext(manager.truncateAndInsertEntities(shows, PersistentRatingShow.class, converter::fromRatingShow)),
                            e -> subscriber.onCompleted(),
                            subscriber::onCompleted
                    );
        }).observeOn(observerScheduler).subscribeOn(Schedulers.io());
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
