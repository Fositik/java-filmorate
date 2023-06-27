package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User createUser(User newUser);

    List<User> getAllUsers();

    Optional<User> getUserById(long id);

    User updateUser(User updatedUser);

    void remove(long id);

    boolean userExists(Long userId);
}