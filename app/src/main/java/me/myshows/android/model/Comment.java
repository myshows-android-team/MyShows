package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Whiplash on 2/9/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {

    private final int userCommentId;
    private final int showId;
    private final int episodeId;
    private final int siteUserId;
    private final EpisodeUser siteUser;
    private final String comment;
    private final String image;
    private final int parentCommentId;
    private final String createdAt;
    private final int statusId;
    private final boolean isNew;
    private final boolean isMyPlus;
    private final boolean isMyMinus;
    private final boolean isMyComment;
    private final int rating;
    private final boolean isBad;
    private final boolean isEditable;

    @JsonCreator
    public Comment(@JsonProperty("userCommentId") int userCommentId, @JsonProperty("showId") int showId,
                   @JsonProperty("episodeId") int episodeId, @JsonProperty("siteUserId") int siteUserId,
                   @JsonProperty("siteUser") EpisodeUser siteUser, @JsonProperty("comment") String comment,
                   @JsonProperty("image") String image, @JsonProperty("parentCommentId") int parentCommentId,
                   @JsonProperty("createdAt") String createdAt, @JsonProperty("statusId") int statusId,
                   @JsonProperty("isNew") boolean isNew, @JsonProperty("isMyPlus") boolean isMyPlus,
                   @JsonProperty("isMyMinus") boolean isMyMinus, @JsonProperty("isMyComment") boolean isMyComment,
                   @JsonProperty("rating") int rating, @JsonProperty("isBad") boolean isBad,
                   @JsonProperty("isEditable") boolean isEditable) {
        this.userCommentId = userCommentId;
        this.showId = showId;
        this.episodeId = episodeId;
        this.siteUserId = siteUserId;
        this.siteUser = siteUser;
        this.comment = comment;
        this.image = image;
        this.parentCommentId = parentCommentId;
        this.createdAt = createdAt;
        this.statusId = statusId;
        this.isNew = isNew;
        this.isMyPlus = isMyPlus;
        this.isMyMinus = isMyMinus;
        this.isMyComment = isMyComment;
        this.rating = rating;
        this.isBad = isBad;
        this.isEditable = isEditable;
    }

    @JsonProperty("userCommentId")
    public int getUserCommentId() {
        return userCommentId;
    }

    @JsonProperty("showId")
    public int getShowId() {
        return showId;
    }

    @JsonProperty("episodeId")
    public int getEpisodeId() {
        return episodeId;
    }

    @JsonProperty("siteUserId")
    public int getSiteUserId() {
        return siteUserId;
    }

    @JsonProperty("siteUser")
    public EpisodeUser getSiteUser() {
        return siteUser;
    }

    @JsonProperty("comment")
    public String getComment() {
        return comment;
    }

    @JsonProperty("iamge")
    public String getImage() {
        return image;
    }

    @JsonProperty("parentCommentId")
    public int getParentCommentId() {
        return parentCommentId;
    }

    @JsonProperty("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("statusId")
    public int getStatusId() {
        return statusId;
    }

    @JsonProperty("isNew")
    public boolean isNew() {
        return isNew;
    }

    @JsonProperty("isMyPlus")
    public boolean isMyPlus() {
        return isMyPlus;
    }

    @JsonProperty("isMyMinus")
    public boolean isMyMinus() {
        return isMyMinus;
    }

    @JsonProperty("isMyComment")
    public boolean isMyComment() {
        return isMyComment;
    }

    @JsonProperty("rating")
    public int getRating() {
        return rating;
    }

    @JsonProperty("isBad")
    public boolean isBad() {
        return isBad;
    }

    @JsonProperty("isEditable")
    public boolean isEditable() {
        return isEditable;
    }
}
