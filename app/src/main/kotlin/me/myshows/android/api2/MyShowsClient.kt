package me.myshows.android.api2

import me.myshows.android.model2.*
import rx.Single

interface MyShowsClient {

    //
    // Profile
    //

    fun profileGet(login: String? = null): Single<Profile>
    fun profileShows(login: String? = null): Single<List<UserShow>>
    fun profileEpisodes(showId: Int): Single<List<UserEpisode>>
    fun profileAchievements(): Single<List<Achievement>>
    fun profileFriends(login: String? = null): Single<List<User>>
    fun profileFollowers(login: String? = null): Single<List<User>>
    fun profileFeed(login: String? = null): Single<List<Feed>>
    fun profileFriendsFeed(): Single<List<Feed>>

}