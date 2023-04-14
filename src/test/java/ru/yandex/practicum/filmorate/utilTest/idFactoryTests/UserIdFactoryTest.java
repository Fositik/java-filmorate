package ru.yandex.practicum.filmorate.utilTest.idFactoryTests;

import org.testng.annotations.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.idfactory.UserIdFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UserIdFactoryTest {

    @Test
    void setIdForUserWhenIdSetIsEmpty() {
        User user1 = new User();
        user1.setEmail("test@test.com");
        user1.setLogin("test");
        user1.setName("Test User");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        List<Long> idSet = new ArrayList<>();
        UserIdFactory.setUniqueUserId(idSet, user1);
        // User result = userService.createUser(user1);
        assertEquals(0, idSet.size());
        assertEquals(1, (long) user1.getId());
    }

    @Test
    void setIdForUserWhenIdSetIsNotEmpty() {
        User user1 = new User();
        user1.setEmail("test@test.com");
        user1.setLogin("test");
        user1.setName("Test User");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        List<Long> idSet = new ArrayList<>();
        UserIdFactory.setUniqueUserId(idSet, user1);
        assertEquals(0, idSet.size());
        assertEquals(1, (long) user1.getId());
        User user2 = new User();
        user2.setEmail("test2@test.com");
        user2.setLogin("test2");
        user2.setName("Test User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        assertEquals(0, idSet.size());
        idSet.add(1L);
        UserIdFactory.setUniqueUserId(idSet, user2);
        assertEquals(2, (long) user2.getId());
    }
}
