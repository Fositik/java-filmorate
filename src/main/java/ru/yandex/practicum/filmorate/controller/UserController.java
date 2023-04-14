package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.validators.UserValidator;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserStorage userStorage;


    private UserController(UserService userService, UserStorage userStorage) {
        this.userService = userService;
        this.userStorage = userStorage;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {
        UserValidator.validate(newUser);
        userService.createUser(newUser);
        log.info("Создание пользователя: {}", newUser);
        return newUser;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return userStorage.getAllUsers();
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) {
        UserValidator.validate(updatedUser);
        userService.updateUser(updatedUser);
        log.info("Обновление пользователя с id={}: {}", updatedUser.getId(), updatedUser);
        return userStorage.updateUser(updatedUser);
    }

    @GetMapping("/{id}")
    public User getUserById(@Valid @RequestBody long id) {
        log.info("Получение пользователя с id={}", id);
        return userStorage.getUserById(id);
    }

    @PostMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(name = "id") Long id, @PathVariable(name = "friendId") Long friendId) {
        log.info("Добавление пользователем с id={} в друзья: пользователя с id={}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable(name = "id") long id, @PathVariable(name = "friendId") long friendId) {
        log.info("Удаление пользователем с id={} из друзей: пользователя с id={}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<Long> getCommonFriends(@PathVariable(name = "id") long id, @PathVariable(name = "otherId") long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/users/{id}/friends")
    public Set<User> getFriends(@PathVariable(name = "id") long id) {
        Set<User> friends = userService.getFriends(id);
        return friends;
    }


}
