package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
   // private final Map<Long, Set<Long>> userFriendIdsMap = new HashMap<>();

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User newUser) throws ValidationException {
       return userStorage.createUser(newUser);
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
    //    userFriendIdsMap.computeIfAbsent(userId, k -> new HashSet<>()).remove(friendId);
    //    userFriendIdsMap.computeIfAbsent(friendId, k -> new HashSet<>()).remove(userId);
        userStorage.removeFriend(userId,friendId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        return userStorage.getCommonFriends(userId,otherId).stream().map(this::getUserById).collect(Collectors.toList());
    //  List<User> userFriends = getFriends(userId);
    //  List<User> friendsOfOtherUser = getFriends(otherId);

    //  return userFriends.stream()
    //          .filter(friendsOfOtherUser::contains)
    //          .collect(Collectors.toList());
    }

    public List<User> getFriends(Long userId) {
      return userStorage.getFriends(userId).stream().map(this::getUserById).collect(Collectors.toList());
//        return userFriendIdsMap.getOrDefault(userId, new HashSet<>())
//                .stream()
//                .map(this::getUserById).filter(Objects::nonNull)
//                .collect(Collectors.toList());
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId,friendId);
//        List<Long> allUserIds = getAllUsers()
//                .stream()
//                .map(User::getId)
//                .collect(Collectors.toList());
//
//        UserValidator.validateExist(allUserIds, friendId);
//
//        userFriendIdsMap.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
//        userFriendIdsMap.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }


}
