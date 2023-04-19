package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.idfactory.UserIdFactory;
import ru.yandex.practicum.filmorate.util.validators.UserValidator;

import java.util.*;


@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User newUser) throws ValidationException {
        List<User> existingUsers = new ArrayList<>(users.values());

        UserValidator.validateCreate(existingUsers, newUser);
        UserIdFactory.setUniqueUserId(newUser);

        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User getUserById(long id) {
        List<Long> reservedIds = new ArrayList<>(users.keySet());
        UserValidator.validateExist(reservedIds, id);
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User remove(long id) {
        UserValidator.validateExist(new ArrayList<>(users.keySet()), id);

        return users.remove(id);
    }


    @Override
    public void addFriend(Long userId, Long friendId) {

    }

    @Override
    public void removeFriend(long userId, long friendId) {

    }

    @Override
    public Set<Long> getCommonFriends(long userId, long otherId) {
        return null;
    }

    @Override
    public Set<Long> getFriends(long user) {
        return null;
    }


    @Override
    public User updateUser(User updatedUser) {
        UserValidator.validateUpdate(new ArrayList<>(users.keySet()), updatedUser);
        User userToUpdate = users.get(updatedUser.getId());
        userToUpdate.setId(userToUpdate.getId());
        userToUpdate.setEmail(updatedUser.getEmail());
        userToUpdate.setLogin(updatedUser.getLogin());
        userToUpdate.setName(updatedUser.getName());
        userToUpdate.setBirthday(updatedUser.getBirthday());
        return userToUpdate;
    }
}