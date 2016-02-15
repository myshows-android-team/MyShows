package me.myshows.android.api;

import java.util.List;
import java.util.Map;

import me.myshows.android.model.EpisodeComments;
import me.myshows.android.model.NextEpisode;
import me.myshows.android.model.RatingShow;
import me.myshows.android.model.Show;
import me.myshows.android.model.ShowEpisode;
import me.myshows.android.model.UnwatchedEpisode;
import me.myshows.android.model.User;
import me.myshows.android.model.UserEpisode;
import me.myshows.android.model.UserFeed;
import me.myshows.android.model.UserShow;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Whiplash
 * @date 14.06.2015
 */
public interface MyShowsApi {

    @GET("/profile/login")
    Observable<Response<ResponseBody>> login(@Query("login") String login, @Query("password") String md5Password);

    @GET("/profile/")
    Observable<User> profile();

    @GET("/profile/{login}")
    Observable<User> profile(@Path("login") String login);

    @GET("/profile/shows/")
    Observable<Map<String, UserShow>> profileShows();

    @GET("/profile/shows/{show_id}/")
    Observable<Map<String, UserEpisode>> profileEpisodesOfShow(@Path("show_id") int showId);

    @GET("/profile/episodes/unwatched/")
    Observable<Map<String, UnwatchedEpisode>> profileUnwatchedEpisodes();

    @GET("/profile/episodes/next/")
    Observable<Map<String, NextEpisode>> profileNextEpisodes();

    @GET("/shows/{show_id}")
    Observable<Show> showInformation(@Path("show_id") int showId);

    @GET("/episodes/{episode_id}")
    Observable<ShowEpisode> episodeInformation(@Path("episode_id") int episodeId);

    @GET("/profile/news/")
    Observable<Map<String, List<UserFeed>>> friendsNews();

    @GET("/shows/top/all/")
    Observable<List<RatingShow>> ratingShows();

    @GET("/profile/comments/episode/{episode_id}")
    Observable<EpisodeComments> comments(@Path("episode_id") int episodeId);
}
