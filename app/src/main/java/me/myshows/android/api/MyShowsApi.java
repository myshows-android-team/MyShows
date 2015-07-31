package me.myshows.android.api;

import java.util.Map;

import me.myshows.android.model.NextEpisode;
import me.myshows.android.model.Show;
import me.myshows.android.model.UnwatchedEpisode;
import me.myshows.android.model.User;
import me.myshows.android.model.UserEpisode;
import me.myshows.android.model.UserShow;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * @author Whiplash
 * @date 14.06.2015
 */
public interface MyShowsApi {

    @GET("/profile/login")
    Observable<Response> login(@Query("login") String login, @Query("password") String md5Password);

    @GET("/profile/")
    Observable<User> profile();

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
}
