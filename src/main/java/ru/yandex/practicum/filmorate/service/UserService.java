package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.validators.UserValidator;

import java.util.*;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> userFriendIdsMap = new HashMap<>();

    @Autowired
    public UserService(UserStorage userStorage) {
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
        List<User> commonFriends = new ArrayList<>(userFriends);
        commonFriends.retainAll(friendsOfOtherUser);
        return commonFriends;
    }

    public List<User> getFriends(Long userId) {
        Set<Long> friendIds = userFriendIdsMap.getOrDefault(userId, new HashSet<>());
        List<User> friends = new ArrayList<>();
        for (Long friendId : friendIds) {
            User friend = getUserById(friendId);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }

    public void addFriend(Long userId, Long friendId) {
        List<User> allUsers = getAllUsers();
        List<Long> allUserIds = new ArrayList<>();
        for (User user : allUsers) {
           Long id = user.getId();
           allUserIds.add(id);
        }
        UserValidator.validateExist(allUserIds,friendId);

        userFriendIdsMap.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        userFriendIdsMap.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);

    }

}