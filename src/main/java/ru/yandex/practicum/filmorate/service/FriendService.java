package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public void removeFriend(Long userId, Long friendId) {
        validateUserId(userId);
        validateUserId(friendId);
        friendStorage.removeFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        validateUserId(userId);
        validateUserId(otherId);
        return friendStorage.getCommonFriends(userId, otherId);
    }

    public List<User> getFriends(Long userId) {
        validateUserId(userId);
        return friendStorage.getFriends(userId);
    }

    public void addFriend(Long userId, Long friendId) {
        validateUserId(userId);
        validateUserId(friendId);
        friendStorage.addFriend(userId, friendId);
    }

    private void validateUserId(Long userId) {
        if (!userStorage.userExists(userId)) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }
}
