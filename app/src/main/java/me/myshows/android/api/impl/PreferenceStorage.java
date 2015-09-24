package me.myshows.android.api.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

import me.myshows.android.api.ClientStorage;

public class PreferenceStorage implements ClientStorage {

    private static final String PREFERENCE_NAME = "my_shows_api_preference";
    private static final String MY_SHOWS_COOKIES = "my_shows_cookies_token";
    private static final String MY_SHOWS_LOGIN = "my_shows_login_token";
    private static final String MY_SHOWS_PASSWORD = "my_shows_password_hash_token";

    private final SharedPreferences preferences;

    public PreferenceStorage(Context context) {
        this.preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public Credentials getCredentials() {
        if (preferences.contains(MY_SHOWS_LOGIN) && preferences.contains(MY_SHOWS_PASSWORD)) {
            String login = preferences.getString(MY_SHOWS_LOGIN, null);
            String passwordHash = preferences.getString(MY_SHOWS_PASSWORD, null);
            return new Credentials(login, passwordHash);
        }
        return null;
    }

    @Override
    public void putCredentials(Credentials credentials) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MY_SHOWS_LOGIN, credentials.getLogin());
        editor.putString(MY_SHOWS_PASSWORD, credentials.getPasswordHash());
        editor.apply();
    }


    @NonNull
    @Override
    public Set<String> getCookies() {
        return preferences.getStringSet(MY_SHOWS_COOKIES, new HashSet<>());
    }

    @Override
    public void putCookies(Set<String> cookieValues) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(MY_SHOWS_COOKIES, cookieValues);
        editor.apply();
    }

    @Override
    public void clear() {
        preferences.edit().clear().apply();
    }
}
