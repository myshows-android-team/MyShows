package me.myshows.android.api;

import java.util.Set;

import me.myshows.android.api.impl.Credentials;

public interface ClientStorage {

    Credentials getCredentials();

    void setCredentials(Credentials credentials);

    Set<String> getCookies();

    void setCookies(Set<String> cookies);
}
