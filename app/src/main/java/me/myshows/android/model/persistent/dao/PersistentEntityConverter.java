package me.myshows.android.model.persistent.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import me.myshows.android.model.Comment;
import me.myshows.android.model.CommentsInformation;
import me.myshows.android.model.Feed;
import me.myshows.android.model.Gender;
import me.myshows.android.model.NextEpisode;
import me.myshows.android.model.RatingEpisode;
import me.myshows.android.model.RatingShow;
import me.myshows.android.model.Show;
import me.myshows.android.model.ShowEpisode;
import me.myshows.android.model.ShowStatus;
import me.myshows.android.model.Statistics;
import me.myshows.android.model.UnwatchedEpisode;
import me.myshows.android.model.User;
import me.myshows.android.model.UserEpisode;
import me.myshows.android.model.UserFeed;
import me.myshows.android.model.UserPreview;
import me.myshows.android.model.UserShow;
import me.myshows.android.model.UserShowEpisodes;
import me.myshows.android.model.WatchStatus;
import me.myshows.android.model.persistent.PersistentCommentsInformation;
import me.myshows.android.model.persistent.PersistentFeed;
import me.myshows.android.model.persistent.PersistentNextEpisode;
import me.myshows.android.model.persistent.PersistentRatingShow;
import me.myshows.android.model.persistent.PersistentShow;
import me.myshows.android.model.persistent.PersistentShowEpisode;
import me.myshows.android.model.persistent.PersistentUnwatchedEpisode;
import me.myshows.android.model.persistent.PersistentUser;
import me.myshows.android.model.persistent.PersistentUserEpisode;
import me.myshows.android.model.persistent.PersistentUserShow;
import me.myshows.android.model.persistent.PersistentUserShowEpisodes;
import me.myshows.android.model.serialization.Marshaller;

public class PersistentEntityConverter {

    private final Marshaller marshaller;

    public PersistentEntityConverter(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public User toUser(PersistentUser persistentUser) {
        try {
            List<UserPreview> friends = marshaller.deserializeList(persistentUser.getFriends(), ArrayList.class, UserPreview.class);
            List<UserPreview> followers = marshaller.deserializeList(persistentUser.getFollowers(), ArrayList.class, UserPreview.class);
            Statistics stats = marshaller.deserialize(persistentUser.getStats(), Statistics.class);
            Gender gender = Gender.fromString(persistentUser.getGender());
            return new User(persistentUser.getLogin(), persistentUser.getAvatarUrl(),
                    persistentUser.getWastedTime(), gender, friends, followers, stats);
        } catch (IOException e) {
            throw new RuntimeException("Unreachable state", e);
        }
    }

    public PersistentUser fromUser(User user) {
        try {
            byte[] friends = marshaller.serialize(user.getFriends());
            byte[] followers = marshaller.serialize(user.getFollowers());
            byte[] stats = marshaller.serialize(user.getStats());
            String gender = user.getGender().toString();
            return new PersistentUser(user.getLogin(), user.getAvatarUrl(), user.getWastedTime(),
                    gender, friends, followers, stats);
        } catch (IOException e) {
            throw new RuntimeException("Unreachable state", e);
        }
    }

    public UserShow toUserShow(PersistentUserShow persistentUserShow) {
        return new UserShow(persistentUserShow.getShowId(), persistentUserShow.getTitle(),
                persistentUserShow.getRuTitle(), persistentUserShow.getRuntime(),
                ShowStatus.fromString(persistentUserShow.getShowStatus()), WatchStatus.fromString(persistentUserShow.getWatchStatus()),
                persistentUserShow.getWatchedEpisodes(), persistentUserShow.getTotalEpisodes(),
                persistentUserShow.getRating(), persistentUserShow.getImage());
    }

    public PersistentUserShow fromUserShow(UserShow userShow) {
        return new PersistentUserShow(userShow.getShowId(), userShow.getTitle(),
                userShow.getRuTitle(), userShow.getRuntime(), userShow.getShowStatus().toString(),
                userShow.getWatchStatus().toString(), userShow.getWatchedEpisodes(), userShow.getTotalEpisodes(),
                userShow.getRating(), userShow.getImage());
    }

    public UserEpisode toUserEpisode(PersistentUserEpisode persistentUserEpisode) {
        return new UserEpisode(persistentUserEpisode.getId(),
                persistentUserEpisode.getWatchDate(), persistentUserEpisode.getRating());
    }

    public PersistentUserEpisode fromUserEpisode(UserEpisode userEpisode) {
        return new PersistentUserEpisode(userEpisode.getId(), userEpisode.getWatchDate(),
                userEpisode.getRating());
    }

    public NextEpisode toNextEpisode(PersistentNextEpisode persistentNextEpisode) {
        return new NextEpisode(persistentNextEpisode.getEpisodeId(),
                persistentNextEpisode.getTitle(), persistentNextEpisode.getShowId(),
                persistentNextEpisode.getSeasonNumber(), persistentNextEpisode.getEpisodeNumber(),
                persistentNextEpisode.getAirDate());
    }

    public PersistentNextEpisode fromNextEpisode(NextEpisode nextEpisode) {
        return new PersistentNextEpisode(nextEpisode.getId(),
                nextEpisode.getTitle(), nextEpisode.getShowId(),
                nextEpisode.getSeasonNumber(), nextEpisode.getEpisodeNumber(),
                nextEpisode.getAirDate());
    }

    public UnwatchedEpisode toUnwatchedEpisode(PersistentUnwatchedEpisode persistentNextEpisodePreview) {
        return new UnwatchedEpisode(persistentNextEpisodePreview.getEpisodeId(),
                persistentNextEpisodePreview.getTitle(), persistentNextEpisodePreview.getShowId(),
                persistentNextEpisodePreview.getSeasonNumber(), persistentNextEpisodePreview.getEpisodeNumber(),
                persistentNextEpisodePreview.getAirDate());
    }

    public PersistentUnwatchedEpisode fromUnwatchedEpisode(UnwatchedEpisode nextEpisodePreview) {
        return new PersistentUnwatchedEpisode(nextEpisodePreview.getId(),
                nextEpisodePreview.getTitle(), nextEpisodePreview.getShowId(),
                nextEpisodePreview.getSeasonNumber(), nextEpisodePreview.getEpisodeNumber(),
                nextEpisodePreview.getAirDate());
    }

    public ShowEpisode toEpisode(PersistentShowEpisode persistentEpisode) {
        try {
            RatingEpisode rating = marshaller.deserialize(persistentEpisode.getRating(), RatingEpisode.class);
            return new ShowEpisode(persistentEpisode.getId(), persistentEpisode.getTitle(),
                    persistentEpisode.getSequenceNumber(), persistentEpisode.getSeasonNumber(),
                    persistentEpisode.getEpisodeNumber(), persistentEpisode.getAirDate(),
                    persistentEpisode.getShortName(), persistentEpisode.getTvrageLink(),
                    persistentEpisode.getImage(), persistentEpisode.getProductionNumber(),
                    persistentEpisode.getTotalWatched(), rating);
        } catch (IOException e) {
            throw new RuntimeException("Unreachable state", e);
        }
    }

    public PersistentShowEpisode fromEpisode(ShowEpisode episode) {
        try {
            byte[] rating = marshaller.serialize(episode.getRating());
            return new PersistentShowEpisode(episode.getId(), episode.getTitle(), episode.getSeasonNumber(),
                    episode.getEpisodeNumber(), episode.getAirDate(), episode.getShortName(),
                    episode.getTvrageLink(), episode.getImage(), episode.getProductionNumber(),
                    episode.getSequenceNumber(), episode.getTotalWatched(), rating);
        } catch (IOException e) {
            throw new RuntimeException("Unreachable state", e);
        }
    }

    public Show toShow(PersistentShow persistentShow) {
        try {
            int[] genres = marshaller.deserialize(persistentShow.getGenres(), int[].class);
            Map<String, ShowEpisode> episodes = toEpisodeMap(persistentShow.getEpisodes());
            String[] images = marshaller.deserialize(persistentShow.getImages(), String[].class);
            return new Show(persistentShow.getId(), persistentShow.getTitle(), persistentShow.getRuTitle(),
                    ShowStatus.fromString(persistentShow.getShowStatus()), persistentShow.getCountry(), persistentShow.getStarted(),
                    persistentShow.getEnded(), persistentShow.getYear(), persistentShow.getKinopoiskId(),
                    persistentShow.getTvrageId(), persistentShow.getImdbId(), persistentShow.getVoted(),
                    persistentShow.getRating(), persistentShow.getRuntime(), persistentShow.getImage(),
                    genres, episodes, persistentShow.getWatching(), images, persistentShow.getDescription());
        } catch (IOException e) {
            throw new RuntimeException("Unreachable state", e);
        }
    }

    public PersistentShow fromShow(Show show) {
        try {
            byte[] genres = marshaller.serialize(show.getGenres());
            RealmList<PersistentShowEpisode> episodes = fromEpisodeMap(show.getEpisodes());
            byte[] images = marshaller.serialize(show.getImages());
            return new PersistentShow(show.getId(), show.getTitle(), show.getRuTitle(),
                    show.getShowStatus().toString(), show.getCountry(), show.getStarted(),
                    show.getEnded(), show.getYear(), show.getKinopoiskId(),
                    show.getTvrageId(), show.getImdbId(), show.getVoted(),
                    show.getRating(), show.getRuntime(), show.getImage(),
                    genres, episodes, show.getWatching(), images, show.getDescription());
        } catch (IOException e) {
            throw new RuntimeException("Unreachable state", e);
        }
    }

    public Feed toFeed(PersistentFeed persistentFeed) {
        try {
            long date = persistentFeed.getDate();
            List<UserFeed> userFeeds = marshaller.deserializeList(persistentFeed.getFeeds(), ArrayList.class, UserFeed.class);
            return new Feed(date, userFeeds);
        } catch (IOException e) {
            throw new RuntimeException("Unreachable state", e);
        }
    }

    public PersistentFeed fromFeed(Feed feed) {
        try {
            long millis = feed.getDate();
            byte[] userFeeds = marshaller.serialize(feed.getFeeds());
            return new PersistentFeed(millis, userFeeds);
        } catch (IOException e) {
            throw new RuntimeException("Unreachable state", e);
        }
    }

    public RatingShow toRatingShow(PersistentRatingShow persistentRatingShow) {
        return new RatingShow(persistentRatingShow.getId(), persistentRatingShow.getTitle(),
                persistentRatingShow.getRuTitle(), ShowStatus.fromString(persistentRatingShow.getShowStatus()),
                persistentRatingShow.getYear(), persistentRatingShow.getRating(),
                persistentRatingShow.getWatching(), persistentRatingShow.getImage(),
                persistentRatingShow.getPlace());
    }

    public PersistentRatingShow fromRatingShow(RatingShow ratingShow) {
        return new PersistentRatingShow(ratingShow.getId(), ratingShow.getTitle(), ratingShow.getRuTitle(),
                ratingShow.getShowStatus().toString(), ratingShow.getYear(), ratingShow.getRating(),
                ratingShow.getWatching(), ratingShow.getImage(), ratingShow.getPlace());
    }

    public UserShowEpisodes toUserShowEpisodes(PersistentUserShowEpisodes userShowEpisodes) {
        List<UserEpisode> episodes = new ArrayList<>(userShowEpisodes.getEpisodes().size());
        for (PersistentUserEpisode episode : userShowEpisodes.getEpisodes()) {
            episodes.add(toUserEpisode(episode));
        }
        return new UserShowEpisodes(userShowEpisodes.getShowId(), episodes);
    }

    public PersistentUserShowEpisodes fromUserShowEpisodes(UserShowEpisodes userShowEpisodes) {
        RealmList<PersistentUserEpisode> episodes = new RealmList<>();
        for (UserEpisode episode : userShowEpisodes.getEpisodes()) {
            episodes.add(fromUserEpisode(episode));
        }
        return new PersistentUserShowEpisodes(userShowEpisodes.getShowId(), episodes);
    }

    public CommentsInformation toCommentsInformation(PersistentCommentsInformation information) {
        try {
            List<Comment> comments = marshaller.deserializeList(information.getComments(), ArrayList.class, Comment.class);
            return new CommentsInformation(information.isTracking(), information.getCount(),
                    information.getNewCount(), information.isShow(), comments);
        } catch (IOException e) {
            throw new RuntimeException("Unreachable state", e);
        }
    }

    public PersistentCommentsInformation fromCommentsInformation(int episodeId, CommentsInformation information) {
        try {
            byte[] comments = marshaller.serialize(information.getComments());
            return new PersistentCommentsInformation(episodeId, information.isTracking(), information.getCount(),
                    information.getNewCount(), information.isShow(), comments);
        } catch (IOException e) {
            throw new RuntimeException("Unreachable state", e);
        }
    }

    private Map<String, ShowEpisode> toEpisodeMap(RealmList<PersistentShowEpisode> persistentEpisodes) {
        if (persistentEpisodes == null) {
            return null;
        }
        Map<String, ShowEpisode> episodes = new HashMap<>();
        for (PersistentShowEpisode persistentEpisode : persistentEpisodes) {
            ShowEpisode episode = toEpisode(persistentEpisode);
            episodes.put(String.valueOf(episode.getId()), episode);
        }
        return episodes;
    }

    private RealmList<PersistentShowEpisode> fromEpisodeMap(Map<String, ShowEpisode> episodes) {
        if (episodes == null) {
            return null;
        }
        RealmList<PersistentShowEpisode> persistentEpisodes = new RealmList<>();
        for (ShowEpisode episode : episodes.values()) {
            persistentEpisodes.add(fromEpisode(episode));
        }
        return persistentEpisodes;
    }
}
