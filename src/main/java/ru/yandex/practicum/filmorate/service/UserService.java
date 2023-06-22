package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.validators.UserValidator;

import java.util.ArrayList;
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
        List<User> allUsers = new ArrayList<>(getAllUsers());
        UserValidator.validateCreate(allUsers, newUser);
        UserValidator.validate(newUser);
        return userStorage.createUser(newUser);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    public User updateUser(User updatedUser) {
        List<Long> allUserIds = getAllUsers()
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());
        UserValidator.validateUpdate(allUserIds, updatedUser);
        return userStorage.updateUser(updatedUser);
    }

    public void remove(Long id) {
        List<Long> allUserIds = getAllUsers()
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());
        UserValidator.validateExist(allUserIds, id);
        userStorage.remove(id);
    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        return userStorage.getCommonFriends(userId, otherId).stream().map(this::getUserById).collect(Collectors.toList());
    }

    public List<User> getFriends(Long userId) {
        return userStorage.getFriends(userId).stream().map(this::getUserById).collect(Collectors.toList());
    }

    public void addFriend(Long userId, Long friendId) {
        List<Long> allUserIds = getAllUsers()
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());
        UserValidator.validateExist(allUserIds, userId);
        UserValidator.validateExist(allUserIds, friendId);
        userStorage.addFriend(userId, friendId);
    }


}
