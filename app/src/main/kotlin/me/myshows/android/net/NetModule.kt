package me.myshows.android.net

import dagger.Module
import dagger.Provides
import me.myshows.android.BuildConfig
import me.myshows.android.storage.TokenStorage
import okhttp3.Interceptor
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
    open fun okHttpClient(cookieManager: CookieManager, tokenStorage: TokenStorage): OkHttpClient {
        val cookieJar = JavaNetCookieJar(cookieManager)

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val authHeaderInterceptor = Interceptor {
            val token = tokenStorage.get()?.accessToken
            val newRequest = it.request().newBuilder().apply {
                if (token != null) addHeader("Authorization", "Bearer $token")
            }.build()
            it.proceed(newRequest)
        }

        return OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authHeaderInterceptor)
                .build()
    }

    @Named("authHost")
    @Singleton
    @Provides
    open fun authHost(): String = AUTH_HOST

    @Named("apiHost")
    @Singleton
    @Provides
    open fun apiHost(): String = API_HOST
}
