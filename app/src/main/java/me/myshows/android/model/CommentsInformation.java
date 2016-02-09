package me.myshows.android.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Whiplash on 2/9/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentsInformation {

    private final boolean isTracking;
    private final int count;
    private final int newCount;
    private final boolean isShow;
    private final List<Comment> comments;

    @JsonCreator
    public CommentsInformation(@JsonProperty("isTracking") boolean isTracking,
                               @JsonProperty("count") int count, @JsonProperty("newCount") int newCount,
                               @JsonProperty("isShow") boolean isShow, @JsonProperty("comments") List<Comment> comments) {
        this.isTracking = isTracking;
        this.count = count;
        this.newCount = newCount;
        this.isShow = isShow;
        this.comments = comments;
    }

    @JsonProperty("isTracking")
    public boolean isTracking() {
        return isTracking;
    }

    @JsonProperty("count")
    public int getCount() {
        return count;
    }

    @JsonProperty("newCount")
    public int getNewCount() {
        return newCount;
    }

    @JsonProperty("isShow")
    public boolean isShow() {
        return isShow;
    }

    @JsonProperty("comments")
    public List<Comment> getComments() {
        return comments;
    }
}
