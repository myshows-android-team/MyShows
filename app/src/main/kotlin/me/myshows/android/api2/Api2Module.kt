package me.myshows.android.api2

import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import me.myshows.android.api2.auth.MyShowsAuthClient
import me.myshows.android.api2.auth.impl.MyShowsAuthClientImpl
import me.myshows.android.api2.client.MyShowsClient
import me.myshows.android.api2.client.impl.MyShowsClientImpl
import me.myshows.android.storage.TokenStorage
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
class Api2Module {

    @Singleton
    @Provides
    fun authClient(@Named("authHost") host: String, okHttpClient: OkHttpClient,
                   mapper: ObjectMapper, tokenStorage: TokenStorage): MyShowsAuthClient =
            MyShowsAuthClientImpl(host, okHttpClient, mapper, tokenStorage)

    @Singleton
    @Provides
    fun apiClient(@Named("apiHost") host: String, okHttpClient: OkHttpClient,
                  mapper: ObjectMapper): MyShowsClient =
            MyShowsClientImpl(host, okHttpClient, mapper)
}
