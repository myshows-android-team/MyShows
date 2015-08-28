package me.myshows.android.model.persistent;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by warrior on 28.08.15.
 */
@RealmClass
public class PersistentUserShowEpisodes extends RealmObject {

    @PrimaryKey
    private int showId;
    private RealmList<PersistentUserEpisode> episodes;

    public PersistentUserShowEpisodes() {
    }

    public PersistentUserShowEpisodes(int showId, RealmList<PersistentUserEpisode> episodes) {
        this.showId = showId;
        this.episodes = episodes;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public RealmList<PersistentUserEpisode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(RealmList<PersistentUserEpisode> episodes) {
        this.episodes = episodes;
    }
}
