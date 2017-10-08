package me.myshows.android.api2

import me.myshows.android.api2.jsonrpc.JsonRPCMethod
import me.myshows.android.api2.jsonrpc.JsonRPCResult
import me.myshows.android.api2.request.Empty
import me.myshows.android.api2.request.ProfileLogin
import me.myshows.android.api2.request.ShowId
import me.myshows.android.model2.*
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Single

interface MyShowsApi {

    //
    // Profile
    //

    @JsonRPCMethod("profile.Get")
    @POST
    fun profileGet(@Body body: ProfileLogin): Single<JsonRPCResult<Profile>>

    @JsonRPCMethod("profile.Shows")
    @POST
    fun profileShows(@Body body: ProfileLogin): Single<JsonRPCResult<List<UserShow>>>

    @JsonRPCMethod("profile.Episodes")
    @POST
    fun profileEpisodes(@Body body: ShowId): Single<JsonRPCResult<List<UserEpisode>>>

    @JsonRPCMethod("profile.Episodes")
    @POST
    fun profileAchievements(@Body body: Empty): Single<JsonRPCResult<List<Achievement>>>

    @JsonRPCMethod("profile.Friends")
    @POST
    fun profileFriends(@Body body: ProfileLogin): Single<JsonRPCResult<List<User>>>

    @JsonRPCMethod("profile.Followers")
    @POST
    fun profileFollowers(@Body body: ProfileLogin): Single<JsonRPCResult<List<User>>>

    @JsonRPCMethod("profile.Feed")
    @POST
    fun profileFeed(@Body body: ProfileLogin): Single<JsonRPCResult<List<Feed>>>

    @JsonRPCMethod("profile.FriendsFeed")
    @POST
    fun profileFriendsFeed(@Body body: Empty): Single<JsonRPCResult<List<Feed>>>
}
