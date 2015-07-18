package me.myshows.android.api.impl;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class Credentials {

    private final String login;
    private final String passwordHash;

    Credentials(String login, String passwordHash) {
        this.login = login;
        this.passwordHash = passwordHash;
    }

    public static Credentials make(String login, String password) {
        return new Credentials(login, new String(Hex.encodeHex(DigestUtils.md5(password))));
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
