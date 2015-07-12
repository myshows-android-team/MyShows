package me.myshows.android.api;

import me.myshows.android.entities.EpisodePreview;
import me.myshows.android.entities.EpisodeRating;
import me.myshows.android.entities.Show;
import me.myshows.android.entities.User;
import me.myshows.android.entities.UserShow;
import rx.Observable;
import rx.Scheduler;

/**
 * Created by warrior on 27.06.15.
 */
public interface MyShowsClient {

    void setObserverScheduler(Scheduler scheduler);

    boolean hasCredential();

    Observable<Boolean> authentication(String login, String password);

    /**
     * Tried to authenticate using credentials from shared preference.
     */
    Observable<Boolean> authentication();

    Observable<User> profile();

    Observable<UserShow> profileShows();

    Observable<EpisodeRating> profileEpisodesOfShow(int showId);

    Observable<EpisodePreview> profileUnwatchedEpisodes();

    Observable<EpisodePreview> profileNextEpisodes();

    Observable<Show> showInformation(int showId);
}
