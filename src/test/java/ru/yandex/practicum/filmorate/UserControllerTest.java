package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    //Тест на создание пользователя с корректными данными
    @Test
    public void testCreateUserWithValidData() {
        UserController userController = new UserController();
        User newUser = new User();
        newUser.setEmail("test@test.com");
        newUser.setLogin("test");
        newUser.setName("Test User");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));
        ResponseEntity<User> response = userController.createUser(newUser);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newUser, response.getBody());
    }


    //Тест на получение списка всех пользователей
    @Test
    public void testGetAllUsers() {
        UserController userController = new UserController();
        User user1 = new User();
        user1.setEmail("test1@test.com");
        user1.setLogin("test1");
        user1.setName("Test User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userController.createUser(user1);
        User user2 = new User();
        user2.setEmail("test2@test.com");
        user2.setLogin("test2");
        user2.setName("Test User 2");
        user2.setBirthday(LocalDate.of(1990, 1, 1));
        userController.createUser(user2);
        ResponseEntity<List<User>> response = userController.getAllUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains(user1));
        assertTrue(response.getBody().contains(user2));
    }


    //Тест на обновление пользователя с корректными данными
    @Test
    public void testUpdateUserWithValidData() {
        UserController userController = new UserController();
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
        ResponseEntity<User> response = userController.updateUser(updatedUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
    }


    //Тест на обновление несуществующего пользователя
    @Test
    public void testUpdateNonExistingUser() {
        UserController userController = new UserController();
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
        UserController userController = new UserController();
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
        UserController userController = new UserController();
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
        UserController userController = new UserController();
        User newUser = new User();
        newUser.setEmail("test@test.com");
        newUser.setLogin("test");
        newUser.setName("");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));
        ResponseEntity<User> response = userController.createUser(newUser);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newUser.getLogin(), response.getBody().getName());
    }

    //Тест на создание пользователя с пустым именем
    @Test
    public void testCreateUserWithNullName() {
        UserController userController = new UserController();
        User newUser = new User();
        newUser.setEmail("test@test.com");
        newUser.setLogin("test");
        newUser.setName(null);
        newUser.setBirthday(LocalDate.of(1990, 1, 1));
        ResponseEntity<User> response = userController.createUser(newUser);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newUser.getLogin(), response.getBody().getName());
    }
}
