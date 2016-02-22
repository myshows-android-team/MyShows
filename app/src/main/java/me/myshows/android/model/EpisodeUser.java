package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Whiplash
 * @date 20.06.2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpisodeUser {

    private final String login;
    private final String avatarUrl;
    private final int wastedTime;
    private final Gender gender;

    @JsonCreator
    public EpisodeUser(@JsonProperty("login") String login, @JsonProperty("avatar") String avatarUrl,
                       @JsonProperty("wastedTime") int wastedTime, @JsonProperty("gender") Gender gender) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.wastedTime = wastedTime;
        this.gender = gender;
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
}
