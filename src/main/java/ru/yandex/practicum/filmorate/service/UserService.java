package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    private final UserStorage userStorage;

    public User createUser(User newUser) {
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
        validateUserId(updatedUser.getId());
        validateAndSetDefaults(updatedUser);
        return userStorage.updateUser(updatedUser);
    }

    public void remove(Long id) {
        validateUserId(id);
        userStorage.remove(id);
    }

    private User validateAndSetDefaults(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Поле 'name' не может быть пустым, оно будет эквивалентно полю 'login'");
        }
        return user;
    }

    public void validateUserId(long userId) {
        if (userId <= 0 || !userStorage.userExists(userId)) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
}
