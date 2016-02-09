package me.myshows.android.model.persistent;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by Whiplash on 2/9/2016.
 */
@RealmClass
public class PersistentCommentsInformation extends RealmObject {

    @PrimaryKey
    private int episodeId;
    private boolean isTracking;
    private int count;
    private int newCount;
    private boolean isShow;
    private byte[] comments;

    public PersistentCommentsInformation() {
    }

    public PersistentCommentsInformation(int episodeId, boolean isTracking, int count, int newCount, boolean isShow, byte[] comments) {
        this.episodeId = episodeId;
        this.isTracking = isTracking;
        this.count = count;
        this.newCount = newCount;
        this.isShow = isShow;
        this.comments = comments;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    public boolean isTracking() {
        return isTracking;
    }

    public void setIsTracking(boolean isTracking) {
        this.isTracking = isTracking;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getNewCount() {
        return newCount;
    }

    public void setNewCount(int newCount) {
        this.newCount = newCount;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }

    public byte[] getComments() {
        return comments;
    }

    public void setComments(byte[] comments) {
        this.comments = comments;
    }
}
