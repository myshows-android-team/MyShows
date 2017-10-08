package me.myshows.android.api2.impl

import com.fasterxml.jackson.databind.ObjectMapper
import me.myshows.android.api2.MyShowsApi
import me.myshows.android.api2.MyShowsClient
import me.myshows.android.api2.jsonrpc.*
import me.myshows.android.api2.request.Empty
import me.myshows.android.api2.request.ProfileLogin
import me.myshows.android.api2.request.ShowId
import me.myshows.android.model2.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.Single

class MyShowsClientImpl(
        okHttpClient: OkHttpClient,
        mapper: ObjectMapper
) : MyShowsClient {

    private val api: MyShowsApi = Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.createAsync())
            .addConverterFactory(JsonRPCConverterFactory.create(mapper))
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
            .create(MyShowsApi::class.java)

    override fun profileGet(login: String?): Single<Profile> =
            api.profileGet(ProfileLogin.new(login)).unwrap()

    override fun profileShows(login: String?): Single<List<UserShow>> =
            api.profileShows(ProfileLogin.new(login)).unwrap()

    override fun profileEpisodes(showId: Int): Single<List<UserEpisode>> =
            api.profileEpisodes(ShowId(showId)).unwrap()

    override fun profileAchievements(): Single<List<Achievement>> =
            api.profileAchievements(Empty).unwrap()

    override fun profileFriends(login: String?): Single<List<User>> =
            api.profileFriends(ProfileLogin.new(login)).unwrap()

    override fun profileFollowers(login: String?): Single<List<User>> =
            api.profileFollowers(ProfileLogin.new(login)).unwrap()

    override fun profileFeed(login: String?): Single<List<Feed>> =
            api.profileFeed(ProfileLogin.new(login)).unwrap()

    override fun profileFriendsFeed(): Single<List<Feed>> =
            api.profileFriendsFeed(Empty).unwrap()

    companion object {
        private const val BASE_URL: String = "https://api.myshows.me/v2/rpc/"
    }
}

private fun <T> Single<JsonRPCResult<T>>.unwrap(): Single<T> = map { result ->
    when (result) {
        is Ok -> result.value
        is Err -> throw JsonRPCException(result.error)
    }
}
