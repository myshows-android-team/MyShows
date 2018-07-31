package me.myshows.android.api2.auth.impl

import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Single
import me.myshows.android.BuildConfig
import me.myshows.android.api2.auth.MyShowsAuth
import me.myshows.android.api2.auth.MyShowsAuthClient
import me.myshows.android.api2.auth.MyShowsAuthClient.RefreshResult.*
import me.myshows.android.storage.TokenStorage
import me.myshows.android.storage.Tokens
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

class MyShowsAuthClientImpl(
        authHost: String,
        okHttpClient: OkHttpClient,
        mapper: ObjectMapper,
        private val tokenStorage: TokenStorage
) : MyShowsAuthClient {

    private val authApi: MyShowsAuth = Retrofit.Builder()
            .baseUrl(authHost)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .build()
            .create(MyShowsAuth::class.java)

    override fun auth(username: String, password: String): Single<Boolean> {
        return authApi.auth(
                clientId = BuildConfig.CLIENT_ID,
                clientSecret = BuildConfig.CLIENT_SECRET,
                username = username,
                password = password
        )
                .doOnSuccess { (accessToken, refreshToken, _) -> tokenStorage.put(Tokens(accessToken, refreshToken)) }
                .map { true }
                // TODO: add error logging
                .onErrorReturn { false }
    }

    override fun refreshTokens(): Single<MyShowsAuthClient.RefreshResult> {
        val refreshToken = tokenStorage.get()?.refreshToken ?: return Single.just(NOTHING_TO_REFRESH)
        return authApi.refresh(
                clientId = BuildConfig.CLIENT_ID,
                clientSecret = BuildConfig.CLIENT_SECRET,
                refreshToken = refreshToken
        )
                .doOnSuccess { (accessToken, refreshToken, _) -> tokenStorage.put(Tokens(accessToken, refreshToken)) }
                .map { SUCCESS }
                // TODO: add error logging
                .onErrorReturn { ERROR }
    }
}
