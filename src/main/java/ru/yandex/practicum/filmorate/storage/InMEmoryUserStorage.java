package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final List<User> users = new ArrayList<>();

    @Override
    public User add(User user) {
        Objects.requireNonNull(user, "User must not be null");
        user.setId(users.size() + 1);
        users.add(user);
        return user;
    }

    @Override
    public Optional<User> getById(int id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst();
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users);
    }

    @Override
    public void remove(int id) {
        users.removeIf(u -> u.getId() == id);
    }

    @Override
    public void update(User updatedUser) {
        Objects.requireNonNull(updatedUser, "Updated user must not be null");
        User currentUser = getById(updatedUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setLogin(updatedUser.getLogin());
        currentUser.setName(updatedUser.getName());
        currentUser.setBirthday(updatedUser.getBirthday());
    }
}