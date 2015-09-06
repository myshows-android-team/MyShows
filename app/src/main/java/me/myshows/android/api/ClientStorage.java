package me.myshows.android.api;

import java.util.Set;

import me.myshows.android.api.impl.Credentials;

public interface ClientStorage {

    Credentials getCredentials();

    void putCredentials(Credentials credentials);

    Set<String> getCookies();

    void putCookies(Set<String> cookies);

    void clean();
}
