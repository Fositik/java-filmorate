package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    User createUser(User newUser) throws ValidationException;

    List<User> getAllUsers();

    User getUserById(long id) throws NotFoundException;

    User updateUser(User updatedUser) throws ValidationException, NotFoundException;

    User remove(long id) throws ValidationException, NotFoundException;

    void addFriend(Long userId, Long friendId);

    void removeFriend(long userId, long friendId);

    Set<Long> getCommonFriends(long userId, long otherId);

    Set<Long> getFriends(long user);
}