package me.myshows.android.api;

import me.myshows.android.api.impl.Credentials;
import me.myshows.android.entity.EpisodePreview;
import me.myshows.android.entity.Show;
import me.myshows.android.entity.User;
import me.myshows.android.entity.UserEpisode;
import me.myshows.android.entity.UserShow;
import rx.Observable;
import rx.Scheduler;

/**
 * Created by warrior on 27.06.15.
 */
public interface MyShowsClient {

    void setObserverScheduler(Scheduler scheduler);

    boolean hasCredentials();

    Observable<Boolean> authentication(Credentials credentials);

    Observable<User> profile();

    Observable<UserShow> profileShows();

    Observable<UserEpisode> profileEpisodesOfShow(int showId);

    Observable<EpisodePreview> profileUnwatchedEpisodes();

    Observable<EpisodePreview> profileNextEpisodes();

    Observable<Show> showInformation(int showId);
}
