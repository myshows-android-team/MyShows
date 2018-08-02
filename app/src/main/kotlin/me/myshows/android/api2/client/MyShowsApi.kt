package me.myshows.android.api2.client

import io.reactivex.Single
import me.myshows.android.api2.jsonrpc.JsonRPCMethod
import me.myshows.android.api2.jsonrpc.JsonRPCResult
import me.myshows.android.api2.request.Empty
import me.myshows.android.api2.request.EpisodeId
import me.myshows.android.api2.request.ProfileLogin
import me.myshows.android.api2.request.ShowId
import me.myshows.android.model2.*
import retrofit2.http.Body
import retrofit2.http.POST

interface MyShowsApi {

    //
    // Profile
    //
    @JsonRPCMethod("profile.Get")
    @POST("/v2/rpc/")
    fun profileGet(@Body body: ProfileLogin): Single<JsonRPCResult<Profile>>

    @JsonRPCMethod("profile.Shows")
    @POST("/v2/rpc/")
    fun profileShows(@Body body: ProfileLogin): Single<JsonRPCResult<List<UserShow>>>

    @JsonRPCMethod("profile.Episodes")
    @POST("/v2/rpc/")
    fun profileEpisodes(@Body body: ShowId): Single<JsonRPCResult<List<UserEpisode>>>

    @JsonRPCMethod("profile.Episodes")
    @POST("/v2/rpc/")
    fun profileAchievements(@Body body: Empty): Single<JsonRPCResult<List<Achievement>>>

    @JsonRPCMethod("profile.Friends")
    @POST("/v2/rpc/")
    fun profileFriends(@Body body: ProfileLogin): Single<JsonRPCResult<List<User>>>

    @JsonRPCMethod("profile.Followers")
    @POST("/v2/rpc/")
    fun profileFollowers(@Body body: ProfileLogin): Single<JsonRPCResult<List<User>>>

    @JsonRPCMethod("profile.Feed")
    @POST("/v2/rpc/")
    fun profileFeed(@Body body: ProfileLogin): Single<JsonRPCResult<List<Feed>>>

    @JsonRPCMethod("profile.FriendsFeed")
    @POST("/v2/rpc/")
    fun profileFriendsFeed(@Body body: Empty): Single<JsonRPCResult<List<Feed>>>

    //
    // Shows
    //
    @JsonRPCMethod("shows.EpisodeComments")
    @POST("/v2/rpc/")
    fun showsEpisodeComments(@Body body: EpisodeId): Single<JsonRPCResult<EpisodeComments>>
}
