package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FriendRequest;
import ru.yandex.practicum.filmorate.model.FriendRequestStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendRequestDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.util.validators.UserValidator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FriendRequestDbStorage friendRequestStorage;

    @Test
        //Тест на создание пользователя
    void createFilm_shouldConfirmThatUserIdExist() {
        //Создаем пользоваеля
        User user = User.builder()
                .id(1L)
                .name("Vladimir")
                .login("Fositik")
                .birthday(LocalDate.now().minusDays(8824))
                .email("fositik@yandex.ru").build();
        userStorage.createUser(user);

        //Получаем пользователя из БД по идентификатору 1
        User userOptional = userStorage.getUserById(1L);

        //Проверяем соответствие идентификатора сохраненного пользователя ожидаемому
        assertEquals(userOptional.getId(), 1);
    }

    @Test
        //Тест на получение пользователя по идентификатору
    void getUserById_shouldConfirmThatUserExist() {
        //Создаем пользователя
        User user = User.builder()
                .id(1L)
                .name("Vladimir")
                .login("Fositik")
                .birthday(LocalDate.now().minusDays(8824))
                .email("fositik@yandex.ru").build();
        userStorage.createUser(user);

        //Получаем пользователя из БД по идентификатору 1
        User userOptional = userStorage.getUserById(1L);

        //Проверка соответствия имени, логина и почты полученного пользователя с ожидаемым
        assertEquals(userOptional.getName(), "Vladimir");
        assertEquals(userOptional.getLogin(), "Fositik");
        assertEquals(userOptional.getEmail(), "fositik@yandex.ru");
    }

    @Test
        //Тест на получение списка всех пользователей
    void getAllUsers_shouldReturnListWithAllUsers() {
        //Создаем двух пользователей
        User user = User.builder()
                .id(1L)
                .name("Vladimir")
                .login("Fositik")
                .birthday(LocalDate.now().minusDays(8824))
                .email("fositik@yandex.ru").build();
        User user2 = User.builder()
                .id(2L)
                .name("Nikita")
                .login("Amigo32")
                .birthday(LocalDate.now().minusDays(8756))
                .email("amigo32@yandex.ru").build();
        userStorage.createUser(user);
        userStorage.createUser(user2);

        //Сохраняем результат выполнения метода getAllUsers() в виде списка пользователей
        List<User> userList = userStorage.getAllUsers();

        //Проверяем соответствие полученных ползователей, проверяя имена
        assertEquals(userList.get(0).getName(), "Vladimir");
        assertEquals(userList.get(1).getName(), "Nikita");
    }

    @Test
    void removeUser_sjouldConfirmThatUserWasRemoved() {
        //Создаем двух пользователей
        User user = User.builder()
                .id(1L)
                .name("Vladimir")
                .login("Fositik")
                .birthday(LocalDate.now().minusDays(8824))
                .email("fositik@yandex.ru").build();
        User user2 = User.builder()
                .id(2L)
                .name("Nikita")
                .login("Amigo32")
                .birthday(LocalDate.now().minusDays(8756))
                .email("amigo32@yandex.ru").build();
        userStorage.createUser(user);
        userStorage.createUser(user2);

        //Удаляем пользователя
        userStorage.remove(1);

        //Сохраняем результат выполнения метода getAllUsers() в виде списка пользователей
        List<Long> userIdList = userStorage.getAllUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList());


        //Проверяем, что первый пользователь был удален, а второй остался
        assertFalse(userIdList.contains(1L));
        assertTrue(userIdList.contains(2L));

        //Проверяем, что первый пользователь действительно удален и выбрасывается исключение NotFoundException
        assertThrows(NotFoundException.class, () -> userStorage.getUserById(1L));
    }

    @Test
    void updateUser_shouldConfirmThatUserWasUpdated() {
        //Создаем пользователя
        User user = User.builder()
                .id(1L)
                .name("Vladimir")
                .login("Fositik")
                .birthday(LocalDate.now().minusDays(8824))
                .email("fositik@yandex.ru").build();
        userStorage.createUser(user);

        //Обновляем пользователя
        User updatedUser = User.builder()
                .id(1L)
                .name("Nikita")
                .login("Amigo32")
                .birthday(LocalDate.now().minusDays(8756))
                .email("amigo32@yandex.ru").build();
        userStorage.updateUser(updatedUser);

        //Получаем пользователя из БД по идентификатору 1
        User userOptional = userStorage.getUserById(1L);

        //Проверяем соответствие идентификатора сохраненного пользователя ожидаемому
        assertEquals(userOptional.getId(), 1);
        assertEquals(userOptional.getName(), "Nikita");
        assertEquals(userOptional.getEmail(), "amigo32@yandex.ru");

        //Проверяем, что при попытке обновления пользователя с несуществующим идентификатором выбрасывается исключение
        User updatedUser2 = User.builder()
                .id(3L)
                .name("Nikita")
                .login("Amigo32")
                .birthday(LocalDate.now().minusDays(8756))
                .email("amigo32@yandex.ru").build();
        assertThrows(NotFoundException.class, () -> userStorage.updateUser(updatedUser2));
    }

    @Test
    void addFriend_shouldConfirmThatUsersAreFriends() throws SQLException {
        //Создаем двух пользователей
        User user = User.builder()
                .id(1L)
                .name("Vladimir")
                .login("Fositik")
                .birthday(LocalDate.now().minusDays(8824))
                .email("fositik@yandex.ru").build();
        User user2 = User.builder()
                .id(2L)
                .name("Nikita")
                .login("Amigo32")
                .birthday(LocalDate.now().minusDays(8756))
                .email("amigo32@yandex.ru").build();
        userStorage.createUser(user);
        userStorage.createUser(user2);

        // создаем заявку на добавление в друзья
        FriendRequest request = friendRequestStorage.addFriendRequest(user.getId(), user2.getId());

        // проверяем, что заявка создана корректно
        assertNotNull(request);
        assertEquals(user.getId(), request.getSenderId());
        assertEquals(user2.getId(), request.getReceiverId());
        assertEquals(FriendRequestStatus.PENDING, request.getStatus());

        // проверяем, что заявка сохранена в базе данных
        FriendRequest requestFromDb = friendRequestStorage.getFriendRequestById(request.getId());
        assertNotNull(requestFromDb);
        assertEquals(request, requestFromDb);
    }

}
