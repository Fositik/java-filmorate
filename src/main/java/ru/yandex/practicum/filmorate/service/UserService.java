package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.FriendRequest;
import ru.yandex.practicum.filmorate.model.FriendRequestStatus;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.validators.UserValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> userFriendIdsMap = new HashMap<>();

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User newUser) throws ValidationException {
        userStorage.createUser(newUser);
        return newUser;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    public User updateUser(User updatedUser) {
        return userStorage.updateUser(updatedUser);
    }

    public User remove(Long id) {
        return userStorage.remove(id);
    }

    public void removeFriend(Long userId, Long friendId) {
        userFriendIdsMap.computeIfAbsent(userId, k -> new HashSet<>()).remove(friendId);
        userFriendIdsMap.computeIfAbsent(friendId, k -> new HashSet<>()).remove(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        List<User> userFriends = getFriends(userId);
        List<User> friendsOfOtherUser = getFriends(otherId);

        return userFriends.stream()
                .filter(friendsOfOtherUser::contains)
                .collect(Collectors.toList());
    }

    public List<User> getFriends(Long userId) {
        return userFriendIdsMap.getOrDefault(userId, new HashSet<>())
                .stream()
                .map(this::getUserById).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void addFriend(Long userId, Long friendId) {
        List<Long> allUserIds = getAllUsers()
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());

        UserValidator.validateExist(allUserIds, friendId);

        userFriendIdsMap.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        userFriendIdsMap.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }


}
