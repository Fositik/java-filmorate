package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    User createUser(User newUser) throws ValidationException;

    List<User> getAllUsers();

    User getUserById(long id);

    User updateUser(User updatedUser);

    User remove(long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(long userId, long friendId);

    Set<Long> getCommonFriends(long userId, long otherId);

    Set<Long> getFriends(long user);
}