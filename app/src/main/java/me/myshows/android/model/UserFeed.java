package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Whiplash on 09.08.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFeed {

    private final int episodeId;
    private final int showId;
    private final String show;
    private final String title;
    private final String login;
    private final Gender gender;
    private final int episodes;
    private final String episode;
    private final Action action;

    public UserFeed(@JsonProperty("episodeId") int episodeId, @JsonProperty("showId") int showId,
                    @JsonProperty("show") String show, @JsonProperty("title") String title,
                    @JsonProperty("login") String login, @JsonProperty("gender") Gender gender,
                    @JsonProperty("episodes") int episodes, @JsonProperty("episode") String episode,
                    @JsonProperty("action") Action action) {
        this.episodeId = episodeId;
        this.showId = showId;
        this.show = show;
        this.title = title;
        this.login = login;
        this.gender = gender;
        this.episodes = episodes;
        this.episode = episode;
        this.action = action;
    }

    @JsonProperty("episodeId")
    public int getEpisodeId() {
        return episodeId;
    }

    @JsonProperty("showId")
    public int getShowId() {
        return showId;
    }

    @JsonProperty("show")
    public String getShow() {
        return show;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("login")
    public String getLogin() {
        return login;
    }

    @JsonProperty("gender")
    public Gender getGender() {
        return gender;
    }

    @JsonProperty("episodes")
    public int getEpisodes() {
        return episodes;
    }

    @JsonProperty("episode")
    public String getEpisode() {
        return episode;
    }

    @JsonProperty("action")
    public Action getAction() {
        return action;
    }
}
