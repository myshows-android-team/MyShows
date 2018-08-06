package me.myshows.android.api

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import me.myshows.android.api.impl.MyShowsClientImpl
import me.myshows.android.api.impl.PreferenceStorage
import okhttp3.OkHttpClient
import rx.android.schedulers.AndroidSchedulers
import java.net.CookieManager
import javax.inject.Singleton

@Module
open class ApiModule {

    @Singleton
    @Provides
    open fun client(context: Context, objectMapper: ObjectMapper, okHttpClient: OkHttpClient, cookieManager: CookieManager): MyShowsClient {
        val storage = PreferenceStorage(context, cookieManager.cookieStore)
        return MyShowsClientImpl(okHttpClient, objectMapper, storage, AndroidSchedulers.mainThread())
    }
}
