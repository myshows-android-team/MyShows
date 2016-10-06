package me.myshows.android.ui.common;

import android.support.annotation.NonNull;

import me.myshows.android.model.AbstractEpisode;

/**
 * Created by warrior on 14.02.16.
 */
public class TestEpisode extends AbstractEpisode {

    private final int id;
    private final int seasonNumber;
    private final int episodeNumber;
    private final String title;
    private final String airDate;

    public TestEpisode(int id, int seasonNumber, int episodeNumber, String title, String airDate) {
        super(airDate);
        this.id = id;
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.airDate = airDate;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getSeasonNumber() {
        return seasonNumber;
    }

    @Override
    public int getEpisodeNumber() {
        return episodeNumber;
    }

    @Override
    public String getAirDate() {
        return airDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestEpisode that = (TestEpisode) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private static final int INVALID_VALUE = -1;

        private int id = INVALID_VALUE;
        private int seasonNumber = INVALID_VALUE;
        private int episodeNumber = INVALID_VALUE;
        private String title;
        private String airDate;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setSeasonNumber(int seasonNumber) {
            this.seasonNumber = seasonNumber;
            return this;
        }

        public Builder setEpisodeNumber(int episodeNumber) {
            this.episodeNumber = episodeNumber;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setAirDate(String airDate) {
            this.airDate = airDate;
            return this;
        }

        public TestEpisode build() {
            int id = checkIntValue(this.id, "id");
            int seasonNumber = checkIntValue(this.seasonNumber, "seasonNumber");
            int episodeNumber = checkIntValue(this.episodeNumber, "episodeNumber");
            return new TestEpisode(id, seasonNumber, episodeNumber, title, airDate);
        }

        private static int checkIntValue(int value, @NonNull String valueName) {
            if (value == INVALID_VALUE) {
                throw new IllegalStateException(String.format("%s is not set", valueName));
            }
            return value;
        }
    }
}
