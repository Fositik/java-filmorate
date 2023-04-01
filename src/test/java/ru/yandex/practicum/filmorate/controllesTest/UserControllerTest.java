package ru.yandex.practicum.filmorate.controllesTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {

    @Autowired
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
    }

    //Тест на создание пользователя с корректными данными
    @Test
    public void testCreateUserWithValidData() {
        User newUser = new User();
        newUser.setEmail("test@test.com");
        newUser.setLogin("test");
        newUser.setName("Test User");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));

       User result = userController.createUser(newUser);

        assertEquals("test@test.com", result.getEmail());
        assertEquals("test", result.getLogin());
        assertEquals("Test User",result.getName());
        assertEquals(LocalDate.of(1990, 1, 1),result.getBirthday());
    }

    //Тест на получение списка всех пользователей
    @Test
    public void testGetAllUsers() {
        User user1 = new User();
        user1.setEmail("test1@test.com");
        user1.setLogin("test1");
        user1.setName("Test User 1");
        user1.setBirthday(LocalDate.of(1999, 1, 1));
        userController.createUser(user1);
        User user2 = new User();
        user2.setEmail("test2@test.com");
        user2.setLogin("test2");
        user2.setName("Test User 2");
        user2.setBirthday(LocalDate.of(1990, 1, 1));
        userController.createUser(user2);

        List<User> result = userController.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("test2@test.com", result.get(1).getEmail());
        assertEquals("test2", result.get(1).getLogin());
        assertEquals("Test User 2",result.get(1).getName());
        assertEquals(LocalDate.of(1990, 1, 1),result.get(1).getBirthday());
        assertEquals("test1@test.com", result.get(0).getEmail());
        assertEquals("test1", result.get(0).getLogin());
        assertEquals("Test User 1",result.get(0).getName());
        assertEquals(LocalDate.of(1999, 1, 1),result.get(0).getBirthday());
    }

    //Тест на обновление пользователя с корректными данными
    @Test
    public void testUpdateUserWithValidData() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("test");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userController.createUser(user);
        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setEmail("updated@test.com");
        updatedUser.setLogin("updated");
        updatedUser.setName("Updated User");
        updatedUser.setBirthday(LocalDate.of(1991, 1, 1));

        User result = userController.createUser(updatedUser);

        assertEquals("updated@test.com", result.getEmail());
        assertEquals("updated", result.getLogin());
        assertEquals("Updated User",result.getName());
        assertEquals(LocalDate.of(1991, 1, 1),result.getBirthday());
    }

    //Тест на обновление несуществующего пользователя
    @Test
    public void testUpdateNonExistingUser() {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setEmail("updated@test.com");
        updatedUser.setLogin("updated");
        updatedUser.setName("Updated User");
        updatedUser.setBirthday(LocalDate.of(1991, 1, 1));
        assertThrows(ValidationException.class, () -> userController.updateUser(updatedUser));
    }

    //Тест на создание пользователя с некорректной датой рождения
    @Test
    public void testCreateUserWithInvalidBirthday() {
        User newUser = new User();
        newUser.setEmail("test@test.com");
        newUser.setLogin("test");
        newUser.setName("Test User");
        newUser.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> userController.createUser(newUser));
    }

    //Тест на создание пользователя с некорректной электронной почтой
    @Test
    public void testCreateUserWithInvalidEmail() {
        User newUser = new User();
        newUser.setEmail("test");
        newUser.setLogin("test");
        newUser.setName("Test User");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));
        assertThrows(ValidationException.class, () -> userController.createUser(newUser));
    }

    //Тест на создание пользователя с отсутствующим полем имени
    @Test
    public void testCreateUserWithBlankName() {
        User newUser = new User();
        newUser.setEmail("test@test.com");
        newUser.setLogin("test");
        newUser.setName("");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));

        User result = userController.createUser(newUser);

        assertEquals("test", result.getName());
    }

    //Тест на создание пользователя с пустым именем
    @Test
    public void testCreateUserWithNullName() {
        User newUser = new User();
        newUser.setEmail("test@test.com");
        newUser.setLogin("test");
        newUser.setName(null);
        newUser.setBirthday(LocalDate.of(1990, 1, 1));

        User result = userController.createUser(newUser);

        assertEquals("test", result.getName());
    }
}
