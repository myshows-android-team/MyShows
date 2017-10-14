package me.myshows.android.storage.impl

import android.content.Context
import android.content.SharedPreferences
import me.myshows.android.storage.TokenStorage
import me.myshows.android.storage.Tokens

class PreferencesTokenStorage(context: Context) : TokenStorage {

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    override fun put(tokens: Tokens) {
        preferences.edit()
                .putString(ACCESS_TOKEN, tokens.accessToken)
                .putString(REFRESH_TOKEN, tokens.refreshToken)
                .apply()
    }

    override fun get(): Tokens? {
        val accessToken = preferences.getString(ACCESS_TOKEN, null) ?: return null
        val refreshToken = preferences.getString(REFRESH_TOKEN, null) ?: return null
        return Tokens(accessToken, refreshToken)
    }

    override fun clear() {
        preferences.edit().clear().apply()
    }

    companion object {
        private val PREFERENCE_NAME = "my_shows_tokens"
        private val ACCESS_TOKEN = "my_shows_access_token"
        private val REFRESH_TOKEN = "my_shows_refresh_token"
    }
}
