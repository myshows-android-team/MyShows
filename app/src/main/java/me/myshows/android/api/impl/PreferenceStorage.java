package me.myshows.android.api.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

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

    @NonNull
    @Override
    public String getLogin() {
        return preferences.getString(MY_SHOWS_LOGIN, "");
    }

    @Override
    public void setLogin(String login) {
        setStringValue(MY_SHOWS_LOGIN, login);
    }

    @NonNull
    @Override
    public String getPasswordHash() {
        return preferences.getString(MY_SHOWS_PASSWORD, "");
    }

    @Override
    public void setPasswordHash(String passwordHash) {
        setStringValue(MY_SHOWS_PASSWORD, passwordHash);
    }

    @NonNull
    @Override
    public Set<String> getCookies() {
        return preferences.getStringSet(MY_SHOWS_COOKIES, new HashSet<>());
    }

    @Override
    public void setCookies(Set<String> cookieValues) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(MY_SHOWS_COOKIES, cookieValues);
        editor.apply();
    }

    @Override
    public boolean containsCredential() {
        return preferences.contains(MY_SHOWS_LOGIN) && preferences.contains(MY_SHOWS_PASSWORD);
    }

    private void setStringValue(String token, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(token, value);
        editor.apply();
    }
}
