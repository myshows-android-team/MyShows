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

    interface MyShowsCallback {
        void getResponse(boolean response);
    }

    void setObserverScheduler(Scheduler scheduler);

    void authentication(String login, String password, MyShowsClient.MyShowsCallback callback);

    boolean isLogin();

    Observable<User> profile();

    Observable<UserShow> profileShows();

    Observable<EpisodeRating> profileEpisodesOfShow(int showId);

    Observable<EpisodePreview> profileUnwatchedEpisodes();

    Observable<EpisodePreview> profileNextEpisodes();

    Observable<Show> showInformation(int showId);
}
