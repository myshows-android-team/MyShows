package me.myshows.android.api.impl;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class Credentials {

    private final String login;
    private final String passwordHash;

    public Credentials(String login, String password) {
        this(login, password, true);
    }

    Credentials(String login, String password, boolean calculateHash) {
        this.login = login;
        if (calculateHash) {
            this.passwordHash = new String(Hex.encodeHex(DigestUtils.md5(password)));
        } else {
            this.passwordHash = password;
        }
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
