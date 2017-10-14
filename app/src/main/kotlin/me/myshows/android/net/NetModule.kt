package me.myshows.android.net

import dagger.Module
import dagger.Provides
import me.myshows.android.BuildConfig
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.net.CookieManager
import javax.inject.Named
import javax.inject.Singleton

@Module
open class NetModule {

    @Singleton
    @Provides
    open fun cookieManager(): CookieManager = CookieManager()

    @Singleton
    @Provides
    open fun okHttpClient(cookieManager: CookieManager): OkHttpClient {
        val cookieJar = JavaNetCookieJar(cookieManager)
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(loggingInterceptor)
                .build()
    }

    @Named("authHost")
    @Singleton
    @Provides
    open fun authHost() = AUTH_HOST
}
