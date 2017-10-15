package me.myshows.android.api2

import rx.Single

interface MyShowsClient {

    //
    // Auth
    //

    fun auth(username: String, password: String): Single<Boolean>
    fun refreshTokens(): Single<Boolean>
}
