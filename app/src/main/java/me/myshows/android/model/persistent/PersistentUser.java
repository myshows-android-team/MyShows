package me.myshows.android.model.persistent;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class PersistentUser extends RealmObject {

    @PrimaryKey
    private String login;
    private String avatarUrl;
    private int wastedTime;
    private String gender;
    private byte[] friends;
    private byte[] followers;
    private byte[] stats;

    public PersistentUser() {
    }

    public PersistentUser(String login, String avatarUrl, int wastedTime, String gender,
                          byte[] friends, byte[] followers, byte[] stats) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.wastedTime = wastedTime;
        this.gender = gender;
        this.friends = friends;
        this.followers = followers;
        this.stats = stats;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getWastedTime() {
        return wastedTime;
    }

    public void setWastedTime(int wastedTime) {
        this.wastedTime = wastedTime;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public byte[] getFriends() {
        return friends;
    }

    public void setFriends(byte[] friends) {
        this.friends = friends;
    }

    public byte[] getFollowers() {
        return followers;
    }

    public void setFollowers(byte[] followers) {
        this.followers = followers;
    }

    public byte[] getStats() {
        return stats;
    }

    public void setStats(byte[] stats) {
        this.stats = stats;
    }
}
