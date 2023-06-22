package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserStorageTest {
    private UserStorage userStorage;

    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void getUserById() {
        User user1 = new User();
        user1.setEmail("test@test.com");
        user1.setLogin("test");
        user1.setName("Test User");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        List<Long> idSet = new ArrayList<>();
        userStorage.createUser(user1);

        assertEquals(1, (long) user1.getId());
        assertEquals(user1, userStorage.getUserById(user1.getId()));

        User user2 = new User();
        user2.setEmail("test1@test.com");
        user2.setLogin("test1");
        user2.setName("Test User1");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        userStorage.createUser(user2);

        assertEquals(user1, userStorage.getUserById(user1.getId()));
    }
}
