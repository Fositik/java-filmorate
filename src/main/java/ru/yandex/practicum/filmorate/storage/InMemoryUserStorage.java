package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.idfactory.UserIdFactory;
import ru.yandex.practicum.filmorate.util.validators.UserValidator;

import java.util.*;


@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
   // private int nextId = 1;
   private TreeSet<Long> usedIds = new TreeSet<>();
    private UserIdFactory userIdFactory;
    @Override
    public User createUser(User newUser) throws ValidationException {
        UserValidator.validateCreate(new ArrayList<>(users.keySet()) ,newUser);
        UserIdFactory.setUniqueUserId(new ArrayList<>(users.keySet()),newUser);
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User getUserById(Long id) {
        UserValidator.validateExist(new ArrayList<>(users.keySet()),id);
        User user = users.get(id);
        //  Optional<User> user = users.stream().filter(u -> u.getId() == id).findFirst();
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь под id=%d не найден", id));
        } else {
            return user;
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User remove(long id) {
        UserValidator.validateExist(new ArrayList<>(users.keySet()),id);
        User removedUser = users.remove(id);
        // Удаляем пользователя из списка друзей его друга
        removedUser.getFriends().forEach(friend -> getUserById(friend.getId()).getFriends().remove(removedUser));
        return removedUser;
    }

    @Override
    public void addFriend(long userId, long friendId) {

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
        UserValidator.validateUpdate(new ArrayList<>(users.keySet()),updatedUser);
        User userToUpdate = users.get(updatedUser.getId());
        userToUpdate.setId(userToUpdate.getId());
        userToUpdate.setEmail(updatedUser.getEmail());
        userToUpdate.setLogin(updatedUser.getLogin());
        userToUpdate.setName(updatedUser.getName());
        userToUpdate.setBirthday(updatedUser.getBirthday());
        return userToUpdate;
    }
}