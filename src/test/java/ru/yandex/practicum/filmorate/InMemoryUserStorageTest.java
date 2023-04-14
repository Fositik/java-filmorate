package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.validators.UserValidator;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryUserStorageTest {


    private InMemoryUserStorage userController;
private UserStorage userStorage;
    @BeforeEach
    public void setUp() {
        userController = new InMemoryUserStorage();
    }

    //Тест на создание пользователя с корректными данными
    @Test
    void testCreateUserWithValidData() {
        User newUser = new User();
        newUser.setEmail("test@test.com");
        newUser.setLogin("test");
        newUser.setName("Test User");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));

        User result = userController.createUser(newUser);
        assertEquals(1, newUser.getId());
        assertEquals("test@test.com", newUser.getEmail());
        assertEquals("test", newUser.getLogin());
        assertEquals("Test User", newUser.getName());
        assertEquals(LocalDate.of(1990, 1, 1), newUser.getBirthday());
    }

    //Тест на получение списка всех пользователей
    @org.testng.annotations.Test
    void testGetAllUsers() {
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
        assertEquals("Test User 2", result.get(1).getName());
        assertEquals(LocalDate.of(1990, 1, 1), result.get(1).getBirthday());
        assertEquals("test1@test.com", result.get(0).getEmail());
        assertEquals("test1", result.get(0).getLogin());
        assertEquals("Test User 1", result.get(0).getName());
        assertEquals(LocalDate.of(1999, 1, 1), result.get(0).getBirthday());
    }

    //Тест на обновление пользователя с корректными данными
    @Test
    void testUpdateUserWithValidData() {
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

        User result = userController.updateUser(updatedUser);

        assertEquals("updated@test.com", result.getEmail());
        assertEquals("updated", result.getLogin());
        assertEquals("Updated User", result.getName());
        assertEquals(LocalDate.of(1991, 1, 1), result.getBirthday());
    }

    //Тест на обновление несуществующего пользователя
    @Test
    void testUpdateNonExistingUser() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("updated@test.com");
        updatedUser.setLogin("updated");
        updatedUser.setName("Updated User");
        updatedUser.setBirthday(LocalDate.of(1991, 1, 1));
        UserValidator.validate(updatedUser);
        assertThrows(NotFoundException.class, () -> userController.updateUser(updatedUser));
    }

    //Тест на создание пользователя с некорректной датой рождения
    @Test
    void testCreateUserWithInvalidBirthday() {
        User newUser = new User();
        newUser.setEmail("test@test.com");
        newUser.setLogin("test");
        newUser.setName("Test User");
        newUser.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> UserValidator.validate(newUser));
    }

    //Тест на создание пользователя с некорректной электронной почтой
    @Test
    void testCreateUserWithInvalidEmail() {
        User newUser = new User();
        newUser.setEmail("test");
        newUser.setLogin("test");
        newUser.setName("Test User");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));
        assertThrows(ValidationException.class, () -> userController.createUser(newUser));
    }

    //Тест на создание пользователя с отсутствующим полем имени
    @Test
    void testCreateUserWithBlankName() {
        User newUser = new User();
        newUser.setEmail("test@test.com");
        newUser.setLogin("test");
        newUser.setName("");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));

       // User result = userController.createUser(newUser);

        assertEquals("test", newUser.getName());
    }

    //Тест на создание пользователя с пустым именем
    @Test
    void testCreateUserWithNullName() {
        User newUser = new User();
        newUser.setEmail("test@test.com");
        newUser.setLogin("test");
        newUser.setName(null);
        newUser.setBirthday(LocalDate.of(1990, 1, 1));

       // User result = userController.createUser(newUser);

        assertEquals("test", newUser.getName());
    }

    @Test
    void testGetUserById1() {
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

        User result = userController.getUserById(1L);


        assertEquals(1, result.getId());
        assertEquals("test1@test.com", result.getEmail());
        assertEquals("test1", result.getLogin());
        assertEquals("Test User 1", result.getName());
        assertEquals(LocalDate.of(1999, 1, 1), result.getBirthday());
    }
}