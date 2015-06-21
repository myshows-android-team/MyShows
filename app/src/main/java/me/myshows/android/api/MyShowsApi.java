package me.myshows.android.api;

import java.util.Map;

import me.myshows.android.entities.EpisodePreview;
import me.myshows.android.entities.EpisodeRating;
import me.myshows.android.entities.User;
import me.myshows.android.entities.UserShow;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * @author Whiplash
 * @date 14.06.2015
 */
public interface MyShowsApi {

    @GET("/profile/login")
    void login(@Query("login") String login, @Query("password") String md5Password, Callback<Response> callback);

    @GET("/profile/")
    void profile(Callback<User> callback);

    @GET("/profile/shows/")
    void profileShows(Callback<Map<String, UserShow>> callback);

    @GET("/profile/shows/{show_id}/")
    void profileEpisodesOfShow(@Path("show_id") int showId, Callback<Map<String, EpisodeRating>> callback);

    @GET("/profile/episodes/unwatched/")
    void profileUnwatchedEpisodes(Callback<Map<String, EpisodePreview>> callback);

    @GET("/profile/episodes/next/")
    void profileNextEpisodes(Callback<Map<String, EpisodePreview>> callback);
}
