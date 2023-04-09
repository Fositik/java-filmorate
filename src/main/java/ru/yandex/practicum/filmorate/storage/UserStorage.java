package ru.yandex.practicum.filmorate.storage;

import java.time.LocalDate;
import java.util.List;

import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    User addUser(String email, String login, String name, LocalDate birthday);

    User getUser(int id) throws EntityNotFoundException;

    List<User> getAllUsers();

    void updateUser(int id, String email, String login, String name, LocalDate birthday)
            throws EntityNotFoundException;

    void deleteUser(int id) throws EntityNotFoundException;
}