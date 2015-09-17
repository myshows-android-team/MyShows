package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Whiplash
 * @date 20.06.2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private final String login;
    private final String avatarUrl;
    private final int wastedTime;
    private final Gender gender;
    private final List<UserPreview> friends;
    private final List<UserPreview> followers;
    private final Statistics stats;

    @JsonCreator
    public User(@JsonProperty("login") String login, @JsonProperty("avatar") String avatarUrl,
                @JsonProperty("wastedTime") int wastedTime, @JsonProperty("gender") Gender gender,
                @JsonProperty("nav_friends") List<UserPreview> friends, @JsonProperty("followers") List<UserPreview> followers,
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
    public Gender getGender() {
        return gender;
    }

    @JsonProperty("nav_friends")
    public List<UserPreview> getFriends() {
        return friends;
    }

    @JsonProperty("followers")
    public List<UserPreview> getFollowers() {
        return followers;
    }

    @JsonProperty("stats")
    public Statistics getStats() {
        return stats;
    }
}
