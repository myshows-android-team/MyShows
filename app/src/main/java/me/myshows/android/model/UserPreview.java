package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by warrior on 19.08.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPreview {

    private final String login;
    private final String avatarUrl;
    private final int wastedTime;
    private final String gender;

    @JsonCreator
    public UserPreview(@JsonProperty("login") String login, @JsonProperty("avatar") String avatarUrl,
                       @JsonProperty("wastedTime") int wastedTime, @JsonProperty("gender") String gender) {
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
    public String getGender() {
        return gender;
    }
}
