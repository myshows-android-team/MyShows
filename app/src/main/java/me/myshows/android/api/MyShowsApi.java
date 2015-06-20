package me.myshows.android.api;

import java.util.Map;

import me.myshows.android.entities.Show;
import me.myshows.android.entities.User;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
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
    void profileShows(Callback<Map<String, Show>> callback);
}
