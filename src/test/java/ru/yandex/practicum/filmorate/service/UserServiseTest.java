package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiseTest {

    private UserService userService;
    private UserStorage userStorage;

    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
     //   userStorage = new InMemoryUserStorage();
    }

    @Test
    void addFriend() {
        User user1 = new User();
        user1.setEmail("test@test.com");
        user1.setLogin("test");
        user1.setName("Test User");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        List<Long> idSet = new ArrayList<>();
        userStorage.createUser(user1);

        User user2 = new User();
        user2.setEmail("test1@test.com");
        user2.setLogin("test1");
        user2.setName("Test User 1");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        userStorage.createUser(user2);

        userService.addFriend(user1.getId(),user2.getId());

        assertEquals(1, userService.getFriends(user1.getId()).size());
     //   assertEquals(user1, userStorage.getUserById(user1.getId()));

        User user3 = new User();
        user3.setEmail("test2@test.com");
        user3.setLogin("test2");
        user3.setName("Test User 2");
        user3.setBirthday(LocalDate.of(1992, 1, 1));
        userStorage.createUser(user3);

        userService.addFriend(user1.getId(),user3.getId());

        assertEquals(2, userService.getFriends(user1.getId()).size());
    }

}
