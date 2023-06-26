package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    User createUser(User newUser);

    List<User> getAllUsers();

    Optional<User> getUserById(long id);

    User updateUser(User updatedUser);

    void remove(long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getCommonFriends(long userId, long otherId);

    Set<Long> getFriends(long user);
}