package me.myshows.android.api2.auth

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Single

interface MyShowsAuth {

    @FormUrlEncoded
    @POST("/oauth/token")
    fun auth(@Field("client_id") clientId: String,
             @Field("client_secret") clientSecret: String,
             @Field("username") username: String,
             @Field("password") password: String,
             @Field("grant_type") grantType: String = "password"): Single<AuthResponse>

    @FormUrlEncoded
    @POST("/oauth/token")
    fun refresh(@Field("client_id") clientId: String,
                @Field("client_secret") clientSecret: String,
                @Field("refresh_token") refreshToken: String,
                @Field("grant_type") grantType: String = "refresh_token"): Single<AuthResponse>
}
