package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;

    @Test
    public void createUser_shouldReturnCreatedUser() {
        // Создаем пользователя
        User user = User.builder()
                .id(1L)
                .name("Nick Name")
                .login("dolore")
                .birthday(LocalDate.parse("1946-08-20"))
                .email("mail@mail.ru")
                .build();

        // Выполняем запрос на создание пользователя
        User createdUser = userStorage.createUser(user);
        System.out.println(user.toString());
        // Проверяем, что созданный пользователь имеет ожидаемые значения полей
        assertEquals(createdUser.getId(), 1L);
        assertEquals(createdUser.getName(), "Nick Name");
        assertEquals(createdUser.getLogin(), "dolore");
        assertEquals(createdUser.getBirthday(), LocalDate.parse("1946-08-20"));
        assertEquals(createdUser.getEmail(), "mail@mail.ru");

    }
        @Test
        public void getUserById_shouldReturnUserWithMatchingId() {
            // Создаем пользователя
            User user = User.builder()
                    .id(1L)
                    .name("Nick Name")
                    .login("dolore")
                    .birthday(LocalDate.parse("1946-08-20"))
                    .email("mail@mail.ru")
                    .build();

            // Выполняем запрос на создание пользователя
            userStorage.createUser(user);

            // Получаем пользователя из БД по идентификатору 1
            User fetchedUser = userStorage.getUserById(1L);
            System.out.println(user.toString());
            // Проверяем, что полученный пользователь имеет ожидаемые значения полей
            assertEquals(fetchedUser.getId(), 1L);
            assertEquals(fetchedUser.getName(), "Nick Name");
            assertEquals(fetchedUser.getLogin(), "dolore");
            assertEquals(fetchedUser.getBirthday(), LocalDate.parse("1946-08-20"));
            assertEquals(fetchedUser.getEmail(), "mail@mail.ru");
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
        System.out.println(userList.toString());

        //Проверяем соответствие полученных ползователей, проверяя имена
        assertEquals(userList.get(0).getName(), "Vladimir");
        assertEquals(userList.get(1).getName(), "Nikita");
    }

    @Test
    void removeUser_shouldConfirmThatUserWasRemoved() {
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
        System.out.println(userIdList);

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
        System.out.println(user.toString());
        //Обновляем пользователя
        User updatedUser = User.builder()
                .id(1L)
                .name("Nikita")
                .login("Amigo32")
                .birthday(LocalDate.now().minusDays(8756))
                .email("amigo32@yandex.ru").build();
        userStorage.updateUser(updatedUser);
        System.out.println(updatedUser.toString());
        //Получаем пользователя из БД по идентификатору 1
        User userOptional = userStorage.getUserById(1L);
        System.out.println(userOptional.toString());

        //Проверяем соответствие идентификатора сохраненного пользователя ожидаемому
        assertEquals(userOptional.getId(), 1);
        assertEquals(userOptional.getName(), "Nikita");
        assertEquals(userOptional.getEmail(), "amigo32@yandex.ru");

        //при попытке обновления пользователя с несуществующим идентификатором
        User updatedUser2 = User.builder()
                .id(3L)
                .name("Nikita")
                .login("Amigo32")
                .birthday(LocalDate.now().minusDays(8756))
                .email("amigo32@yandex.ru").build();
        System.out.println(updatedUser2.toString());
        userStorage.updateUser(updatedUser2);
        assertThrows(NotFoundException.class, () -> userStorage.getUserById(updatedUser2.getId()));
    }



}
