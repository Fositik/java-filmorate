package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    void addFriend(Long userId, Long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getCommonFriends(long userId, long otherId);

    List<User> getFriends(long user);
}
