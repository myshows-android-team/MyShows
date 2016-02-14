package me.myshows.android.model;

/**
 * Created by warrior on 04.11.15.
 */
public interface Episode {

    int getId();

    String getTitle();

    int getSeasonNumber();

    int getEpisodeNumber();

    String getAirDate();

    long getAirDateInMillis();

    boolean isSpecial();
}
