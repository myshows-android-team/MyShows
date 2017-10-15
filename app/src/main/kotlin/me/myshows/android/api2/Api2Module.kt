package me.myshows.android.api2

import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import me.myshows.android.api2.impl.MyShowsClientImpl
import me.myshows.android.storage.TokenStorage
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
class Api2Module {

    @Singleton
    @Provides
    fun client(@Named("authHost") host: String, okHttpClient: OkHttpClient,
               mapper: ObjectMapper, tokenStorage: TokenStorage): MyShowsClient =
            MyShowsClientImpl(host, okHttpClient, mapper, tokenStorage)
}
