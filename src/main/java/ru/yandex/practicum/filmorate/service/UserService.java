package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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
        List<User> allUsers = userStorage.getAllUsers();
        return allUsers;
    }

    public User getUserById(Long userId) {
        User user = userStorage.getUserById(userId);
        return user;
    }

    public User updateUser(User updatedUser) {
        userStorage.updateUser(updatedUser);
        return updatedUser;
    }

    public User remove(Long id) {
        User removedUser = userStorage.remove(id);
        return removedUser;
    }

//    public void addFriend(Long userId, Long friendId) {
//        userFriendIdsMap.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
//        userFriendIdsMap.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
//    }

    public void removeFriend(Long userId, Long friendId) {
        userFriendIdsMap.computeIfAbsent(userId, k -> new HashSet<>()).remove(friendId);
        userFriendIdsMap.computeIfAbsent(friendId, k -> new HashSet<>()).remove(userId);
    }

    public Set<Long> getCommonFriends(Long userId, Long otherId) {
        Set<Long> friendsOfUser = userFriendIdsMap.getOrDefault(userId, new HashSet<>());
        //Получаем набор друзей пользователя
        Set<Long> friendsOfUserFriend = userFriendIdsMap.getOrDefault(otherId, new HashSet<>());
        //Получаем набор друзей другого пользователя
        Set<Long> commonFriends = new HashSet<>(friendsOfUser);
        //Создаем новый набор commonFriends, инициализированный набором друзей пользователя
        commonFriends.retainAll(friendsOfUserFriend);
        //Метод retainAll меняет commonFriends так, чтобы в нем остались только общие элементы
        return commonFriends;
    }

    public Set<User> getFriends(Long user) {
        Set<Long> friendIds = userFriendIdsMap.getOrDefault(user, new HashSet<>());
        Set<User> friends = new HashSet<>();
        for (Long friendId : friendIds) {
            Set<Long> friendFriendIds = userFriendIdsMap.getOrDefault(friendId, new HashSet<>());
            if (friendFriendIds.contains(user)) {
                friends.add(getUserById(friendId));
            }
        }
        return friends;
    }
    public void addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        Set<Long> friends = userFriendIdsMap.getOrDefault(user.getId(), new HashSet<>());
        friends.add(friend.getId());
        userFriendIdsMap.put(user.getId(), friends);
    }

}