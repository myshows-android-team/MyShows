package me.myshows.android.dao.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import me.myshows.android.entity.Statistics;
import me.myshows.android.entity.User;
import me.myshows.android.serialization.Marshaller;

public class PersistentEntityConverter {

    private final Marshaller marshaller;

    public PersistentEntityConverter(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public User toUser(PersistentUser persistentUser) {
        try {
            List<User> friends = toUserList(persistentUser.getFriends());
            List<User> followers = toUserList(persistentUser.getFollowers());
            Statistics stats = marshaller.deserialize(persistentUser.getStats(), Statistics.class);
            return new User(persistentUser.getLogin(), persistentUser.getAvatarUrl(),
                    persistentUser.getWastedTime(), persistentUser.getGender(),
                    friends, followers, stats);
        } catch (IOException e) {
            throw new RuntimeException("Unreachable state");
        }
    }

    public PersistentUser fromUser(User user) {
        try {
            RealmList<PersistentUser> friends = fromUserList(user.getFriends());
            RealmList<PersistentUser> followers = fromUserList(user.getFollowers());
            byte[] stats = marshaller.serialize(user.getStats());
            return new PersistentUser(user.getLogin(), user.getAvatarUrl(), user.getWastedTime(),
                    user.getGender(), friends, followers, stats);
        } catch (IOException e) {
            throw new RuntimeException("Unreachable state");
        }
    }

    private List<User> toUserList(RealmList<PersistentUser> persistentUsers) {
        List<User> users = new ArrayList<>();
        for (PersistentUser persistentUser : persistentUsers) {
            users.add(toUser(persistentUser));
        }
        return users;
    }

    private RealmList<PersistentUser> fromUserList(List<User> users) {
        RealmList<PersistentUser> persistentUsers = new RealmList<>();
        for (User user : users) {
            persistentUsers.add(fromUser(user));
        }
        return persistentUsers;
    }
}
