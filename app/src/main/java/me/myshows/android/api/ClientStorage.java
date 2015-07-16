package me.myshows.android.api;

import java.util.Set;

public interface ClientStorage {

    String getLogin();

    void setLogin(String login);

    String getPasswordHash();

    void setPasswordHash(String passwordHash);

    Set<String> getCookies();

    void setCookies(Set<String> cookies);

    boolean containsCredential();
}
