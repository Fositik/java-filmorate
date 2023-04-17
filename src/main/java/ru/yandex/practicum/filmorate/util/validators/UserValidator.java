package ru.yandex.practicum.filmorate.util.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class UserValidator {
    private static final Pattern rfc2822 = Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

    public static void validate(User user) throws ValidationException {
        if (user.getBirthday().isAfter(LocalDate.now())
        ) {
            log.debug("Дата рождения не может быть в будущем.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        } else if (!rfc2822.matcher(user.getEmail()).matches()) {
            log.debug("Электронная почта не может быть пустой и должна содержать символ @.");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Поле 'name' отсутствует. " +
                    "Так как поле 'name' не может быть пустым, оно будет эквивалентно полю 'login'");
            user.setName(user.getLogin());
        }
//        if (user.getFriends() == null){
//            log.info("Пользователь не имеет друзей");
//            user.setFriends(null);
//        }
    }

    public static void validateCreate(List<User> existingUsers, User createdUser) throws ValidationException {
        if (createdUser.getId() != null) {
            if (existingUsers.stream().anyMatch(u -> u.getId().equals(createdUser.getId()))) {
                throw new ValidationException("Пользователь уже существует");
            }
            if (existingUsers.stream().anyMatch(u -> u.getEmail().equals(createdUser.getEmail()))) {
                throw new ValidationException("Пользователь с таким email уже существует");
            }
            if (existingUsers.stream().anyMatch(u -> u.getLogin().equals(createdUser.getLogin()))) {
                throw new ValidationException("Пользователь с таким логином уже существует");
            }
        }
    }

    public static void validateUpdate(List<Long> userIds, User updatedUser)
            throws ValidationException, NotFoundException {
        if (updatedUser.getId() == null) {
            log.error("Пользователь еще не был создан: {}", updatedUser);
            throw new ValidationException("Пользователь еще не был создан");
        }
        if (!userIds.contains(updatedUser.getId())) {
            log.error("Пользователь еще не был создан: {}", updatedUser);
            throw new NotFoundException("Пользователь еще не был создан");
        }
    }

    public static void validateExist(List<Long> reservedIds, long id) throws NotFoundException {
        userIncorrectId(id);
        if (!reservedIds.contains(id)) {
            throw new NotFoundException(String.format("Пользователь под id %s не найден", id));
        }
    }

    public static void userIncorrectId(long userId) {
        if (userId <= 0) {
            log.error("Некорректный идентификатор пользователя: {}", userId);
            throw new NotFoundException("Некорректный идентификатор пользователя");
        }
    }
}
