package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface FriendStorage {
    void addFriend(Long userId, Long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getCommonFriends(long userId, long otherId);

    Set<Long> getFriends(long user);
}
