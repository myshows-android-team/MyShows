package me.myshows.android.api;

import me.myshows.android.api.impl.Credentials;

public interface ClientStorage {

    Credentials getCredentials();

    void putCredentials(Credentials credentials);

    void clear();
}
