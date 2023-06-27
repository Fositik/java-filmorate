package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendService {
    private final FriendStorage friendStorage;
    private final UserService userService;

    public void removeFriend(Long userId, Long friendId) {
        userService.validateUserId(userId);
        userService.validateUserId(friendId);
        friendStorage.removeFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        userService.validateUserId(userId);
        userService.validateUserId(otherId);
        return friendStorage.getCommonFriends(userId, otherId);
    }

    public List<User> getFriends(Long userId) {
        userService.validateUserId(userId);
        return friendStorage.getFriends(userId).stream()
                .map(userService::getUserById)
                .collect(Collectors.toList());
    }

    public void addFriend(Long userId, Long friendId) {
        userService.validateUserId(userId);
        userService.validateUserId(friendId);
        friendStorage.addFriend(userId, friendId);
    }
}
