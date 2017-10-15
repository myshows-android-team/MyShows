package me.myshows.android.storage

interface TokenStorage {
    fun put(tokens: Tokens)
    fun get(): Tokens?
    fun clear()
}

data class Tokens(val accessToken: String, val refreshToken: String)
