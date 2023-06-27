package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {
        log.info("Создание пользователя: {}", newUser);
        User createdUser = userService.createUser(newUser);
        log.info("Пользователь создан: {}", createdUser);
        return createdUser;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.info("Обновление пользователя с id={}: {}", updatedUser.getId(), updatedUser);
        return userService.updateUser(updatedUser);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") long id) {
        log.info("Получение пользователя с id={}", id);
        return userService.getUserById(id);
    }
}
