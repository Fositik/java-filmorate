package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private List<User> users = new ArrayList<>();
    private int nextId = 1;
    //На просторах интернета наткнулся на паттерн для проверки корректности ввода Email
    private static final Pattern rfc2822 = Pattern.compile(
            "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
    );

    /**
     * Добавляет нового пользователя
     *
     * @param newUser объект User {@link User}, который содержит данные пользователя
     * @return возвращает объект ResponseEntity<User> с кодом состояния HTTP 201 CREATED и добавленным пользователем в теле ответа.
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User newUser) {
        log.info("Создание пользователя: {}", newUser);
        validate(newUser, "Error");
        newUser.setId(nextId++);
        users.add(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    /**
     * Возвращает список всех пользователей
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return ResponseEntity.ok(users);
    }

    /**
     * * Обновляет данные фильма
     * * @param updatedUser объект User {@link User}, который содержит данные обновленного пользователя
     * * @return объект ResponseEntity, содержащий обновленный фильм и статус ответа HTTP 200 OK
     * * @throws ValidationException если форма обновления пользователя заполнена неправильно или пользователь с указанным id не найден
     */
    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User updatedUser) {
        validate(updatedUser, "User update form is filled in incorrectly");
        log.info("Обновление пользователя с id={}: {}", updatedUser.getId(), updatedUser);
        User userToUpdate = users.stream().filter(u -> u.getId() == updatedUser.getId()).findFirst()
                .orElseThrow(() -> new ValidationException("Пользователь с id=" + updatedUser.getId() + " не найден"));
        userToUpdate.setEmail(updatedUser.getEmail());
        userToUpdate.setLogin(updatedUser.getLogin());
        userToUpdate.setName(updatedUser.getName());
        userToUpdate.setBirthday(updatedUser.getBirthday());
        return ResponseEntity.ok(userToUpdate);
    }

    /**
     * Метод проверяет соответствие данных пользователя следующим условиям:
     * - Имя пользователя не может быть пустым.
     * -- Если имя пользователя отсутствует, то оно будет соответствовать логину.
     * - Дата рождения пользователя не может быть позже настоящего момента времени.
     * @param user    объект User {@link User}, который содержит данные проверяемого на соответствие условиям пользователя
     * @param message сообщение об ошибке, если объект не соответствует условиям
     */
    private void validate(User user, String message) {
        if (user.getBirthday().isAfter(LocalDate.now())                 //Дата рождения не может быть в будущем.
                || !rfc2822.matcher(user.getEmail()).matches()           //Электронная почта не может быть пустой и должна содержать символ @.
        ) {
            log.debug(message);
            throw new ValidationException(message);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
