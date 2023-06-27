package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    private final UserStorage userStorage;

    public User createUser(User newUser) {
        // Проверяем и обновляем поле name, если оно пустое или null
        validateAndSetDefaults(newUser);
        return userStorage.createUser(newUser);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long userId) {
        return userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с указанным " +
                "ID не найден: " + userId));
    }

    public User updateUser(User updatedUser) {
        return userStorage.updateUser(updatedUser);
    }

    public void remove(Long id) {
        userStorage.remove(id);
    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    public List<User> getFriends(Long userId) {
        return userStorage.getFriends(userId).stream().map(this::getUserById).collect(Collectors.toList());
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    private User validateAndSetDefaults(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Поле 'name' не может быть пустым, оно будет эквивалентно полю 'login'");
        }
        return user;
    }

}
