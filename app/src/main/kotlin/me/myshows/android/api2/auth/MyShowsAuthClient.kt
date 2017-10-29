package me.myshows.android.api2.auth

import rx.Single

interface MyShowsAuthClient {
    fun auth(username: String, password: String): Single<Boolean>
    fun refreshTokens(): Single<RefreshResult>

    enum class RefreshResult {
        NOTHING_TO_REFRESH,
        SUCCESS,
        ERROR
    }
}
