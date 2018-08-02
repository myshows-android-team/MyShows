package me.myshows.android.api;

import java.util.List;

import me.myshows.android.api.impl.Credentials;
import me.myshows.android.model.EpisodeInformation;
import me.myshows.android.model.Feed;
import me.myshows.android.model.NextEpisode;
import me.myshows.android.model.RatingShow;
import me.myshows.android.model.Show;
import me.myshows.android.model.UnwatchedEpisode;
import me.myshows.android.model.User;
import me.myshows.android.model.UserShow;
import me.myshows.android.model.UserShowEpisodes;
import rx.Observable;
import rx.Single;

/**
 * Created by warrior on 27.06.15.
 */
public interface MyShowsClient {

    boolean hasCredentials();

    void clear();

    Single<Boolean> authentication(Credentials credentials);

    Single<Boolean> autoAuthentication();

    Observable<User> profile();

    Observable<User> profile(String login);

    Observable<List<UserShow>> profileShows();

    Observable<UserShowEpisodes> profileEpisodesOfShow(int showId);

    Observable<List<UnwatchedEpisode>> profileUnwatchedEpisodes();

    Observable<List<NextEpisode>> profileNextEpisodes();

    Observable<Show> showInformation(int showId);

    Observable<EpisodeInformation> episodeInformation(int episodeId);

    Observable<List<Feed>> friendsNews();

    Observable<List<RatingShow>> ratingShows();
}
