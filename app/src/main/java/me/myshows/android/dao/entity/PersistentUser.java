package me.myshows.android.dao.entity;

import io.realm.RealmList;
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
    private RealmList<PersistentUser> friends;
    private RealmList<PersistentUser> followers;
    private byte[] stats;

    public PersistentUser() {
    }

    public PersistentUser(String login, String avatarUrl, int wastedTime, String gender,
                          RealmList<PersistentUser> friends, RealmList<PersistentUser> followers,
                          byte[] stats) {
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

    public RealmList<PersistentUser> getFriends() {
        return friends;
    }

    public void setFriends(RealmList<PersistentUser> friends) {
        this.friends = friends;
    }

    public RealmList<PersistentUser> getFollowers() {
        return followers;
    }

    public void setFollowers(RealmList<PersistentUser> followers) {
        this.followers = followers;
    }

    public byte[] getStats() {
        return stats;
    }

    public void setStats(byte[] stats) {
        this.stats = stats;
    }
}
