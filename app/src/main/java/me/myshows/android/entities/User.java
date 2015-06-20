package me.myshows.android.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Whiplash
 * @date 20.06.2015
 */
public class User {

    private final String login;
    private final String avatarUrl;
    private final int wastedTime;
    private final String gender;
    private final List<User> friends;
    private final List<User> followers;
    private final Statistics stats;

    @JsonCreator
    public User(@JsonProperty("login") String login, @JsonProperty("avatar") String avatarUrl,
                @JsonProperty("wastedTime") int wastedTime, @JsonProperty("gender") String gender,
                @JsonProperty("friends") List<User> friends, @JsonProperty("followers") List<User> followers,
                @JsonProperty("stats") Statistics stats) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.wastedTime = wastedTime;
        this.gender = gender;
        this.friends = friends;
        this.followers = followers;
        this.stats = stats;
    }

    @JsonProperty("login")
    public String getLogin() {
        return login;
    }

    @JsonProperty("avatar")
    public String getAvatarUrl() {
        return avatarUrl;
    }

    @JsonProperty("wastedTime")
    public int getWastedTime() {
        return wastedTime;
    }

    @JsonProperty("gender")
    public String getGender() {
        return gender;
    }

    @JsonProperty("friends")
    public List<User> getFriends() {
        return friends;
    }

    @JsonProperty("followers")
    public List<User> getFollowers() {
        return followers;
    }

    @JsonProperty("stats")
    public Statistics getStats() {
        return stats;
    }
}
