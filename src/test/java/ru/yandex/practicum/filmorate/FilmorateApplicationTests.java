package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class FilmorateApplicationTests {

    private final UserService userService;

    private final FilmService filmService;

    private final GenreService genreService;

    private final MpaService mpaService;

    private final FriendService friendService;

    private User createUser(Long id, String name, String login, LocalDate birthday, String email) {
        return User.builder()
                .id(id)
                .name(name)
                .login(login)
                .birthday(birthday)
                .email(email)
                .build();
    }

    private Film createFilm(Long id,
                            String name,
                            String description,
                            LocalDate releaseDate,
                            Integer duration,
                            LinkedHashSet<Genre> genres,
                            RatingMPA mpa) {
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .genres(genres)
                .mpa(mpa)
                .build();
    }

    @Test
    public void createUser_shouldReturnCreatedUser() {
        // Создаем пользователя
        User createdUser = createUser(1L, "Nick Name", "dolore",
                LocalDate.parse("1946-08-20"), "mail@mail.ru");
        userService.createUser(createdUser);

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
        User createdUser = createUser(1L, "Nick Name", "dolore",
                LocalDate.parse("1946-08-20"), "mail@mail.ru");
        userService.createUser(createdUser);

        // Получаем пользователя из БД по идентификатору 1
        User fetchedUser = userService.getUserById(1L);
        System.out.println(createdUser.toString());

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
        User user = createUser(1L, "Vladimir", "Foiik",
                LocalDate.now().minusDays(8824), "foiik@yandex.ru");
        userService.createUser(user);

        User user2 = createUser(2L, "Nikita", "Amigo32",
                LocalDate.now().minusDays(8756), "amigo32@yandex.ru");
        userService.createUser(user2);

        //Сохраняем результат выполнения метода getAllUsers() в виде списка пользователей
        List<User> userList = userService.getAllUsers();
        System.out.println(userList.toString());

        //Проверяем соответствие полученных ползователей, проверяя имена
        assertEquals(userList.get(0).getName(), "Vladimir");
        assertEquals(userList.get(1).getName(), "Nikita");
    }

    @Test
    void removeUser_shouldConfirmThatUserWasRemoved() {
        //Создаем двух пользователей
        User user = createUser(1L, "Vladimir", "Foiik",
                LocalDate.now().minusDays(8824), "foiik@yandex.ru");
        userService.createUser(user);

        User user2 = createUser(2L, "Nikita", "Amigo32",
                LocalDate.now().minusDays(8756), "amigo32@yandex.ru");
        userService.createUser(user2);

        //Удаляем пользователя
        userService.remove(1L);

        //Сохраняем результат выполнения метода getAllUsers() в виде списка пользователей
        List<Long> userIdList = userService.getAllUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList());
        System.out.println(userIdList);

        //Проверяем, что первый пользователь был удален, а второй остался
        assertFalse(userIdList.contains(1L));
        assertTrue(userIdList.contains(2L));

        //Проверяем, что первый пользователь действительно удален и выбрасывается исключение NotFoundException
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_shouldConfirmThatUserWasUpdated() {
        //Создаем двух пользователей
        User user = createUser(1L, "Vladimir", "Foiik",
                LocalDate.now().minusDays(8824), "foiik@yandex.ru");
        userService.createUser(user);

        User updatedUser = createUser(1L, "Nikita", "Amigo32",
                LocalDate.now().minusDays(8756), "amigo32@yandex.ru");

        userService.updateUser(updatedUser);
        System.out.println(updatedUser.toString());
        //Получаем пользователя из БД по идентификатору 1
        User userOptional = userService.getUserById(1L);
        System.out.println(userOptional.toString());

        //Проверяем соответствие идентификатора сохраненного пользователя ожидаемому
        assertEquals(userOptional.getId(), 1);
        assertEquals(userOptional.getName(), "Nikita");
        assertEquals(userOptional.getEmail(), "amigo32@yandex.ru");
    }

    @Test
    void updateUser_shouldReturnNotFoundException() {
        //при попытке обновления пользователя с несуществующим идентификатором
        User updatedUser2 = createUser(3L, "Nikita", "Amigo32",
                LocalDate.now().minusDays(8756), "amigo32@yandex.ru");

        System.out.println(updatedUser2.toString());
        assertThrows(NotFoundException.class, () -> userService.getUserById(updatedUser2.getId()));
    }

    @Test
    void friendship_shouldConfirmThatUserIsFriendByUser2() {
        //Создаем двух пользователей
        User user = createUser(1L, "Vladimir", "Foiik",
                LocalDate.now().minusDays(8824), "foiik@yandex.ru");
        userService.createUser(user);

        User user2 = createUser(2L, "Nikita", "Amigo32",
                LocalDate.now().minusDays(8756), "amigo32@yandex.ru");
        userService.createUser(user2);

        //Добавляем пользователя 2 в друзья
        friendService.addFriend(user.getId(), user2.getId());

        //Проверяем, что пользователь 2 есть в списке наших друзей
        List<User> userFriend = friendService.getFriends(user.getId());
        assertEquals(userFriend.size(), 1);

        //Так как дружба у нас односторонняя, то список друзей у пользователя 2 должен быть пуст
        List<User> user2Friend = friendService.getFriends(user2.getId());
        assertEquals(user2Friend.size(), 0);
    }

    @Test
    void friendship_shouldConfirmThatFriendWasRemoved() {
        //Создаем двух пользователей
        User user = createUser(1L, "Vladimir", "Foiik",
                LocalDate.now().minusDays(8824), "foiik@yandex.ru");
        userService.createUser(user);

        User user2 = createUser(2L, "Nikita", "Amigo32",
                LocalDate.now().minusDays(8756), "amigo32@yandex.ru");
        userService.createUser(user2);

        //Добавляем пользователя 2 в друзья
        friendService.addFriend(user.getId(), user2.getId());

        //Проверяем, что пользователь 2 есть в списке наших друзей
        List<User> userFriend = friendService.getFriends(user.getId());
        assertEquals(userFriend.size(), 1);

        friendService.removeFriend(user.getId(), user2.getId());
        List<User> userFriendEmptyList = friendService.getFriends(user.getId());
        assertEquals(userFriendEmptyList.size(), 0);
    }


    @Test
    void friendship_shouldReturnCommonFriends() {
        //Создаем пользователей
        User user = createUser(1L, "Vladimir", "Foiik",
                LocalDate.now().minusDays(8824), "foiik@yandex.ru");
        userService.createUser(user);
        System.out.println(user);

        User user2 = createUser(2L, "Nikita", "Amigo32",
                LocalDate.now().minusDays(8756), "amigo32@yandex.ru");
        userService.createUser(user2);
        System.out.println(user2);

        User user3 = createUser(0L, "Sergey", "Gey32",
                LocalDate.now().minusDays(8456), "ahegaoo32@yandex.ru");
        userService.createUser(user3);
        System.out.println(user3);

        //add friendships
        friendService.addFriend(user.getId(), user3.getId());
        friendService.addFriend(user2.getId(), user3.getId());

        //get common friends
        List<User> commonFriend = friendService.getCommonFriends(user.getId(), user2.getId());
        assertEquals(commonFriend.size(), 1);
        assertEquals(commonFriend.get(0), user3);
    }

    @Test
    public void createFilm_shouldReturnCreatedFilm() {
        // Создаем фильм
        Genre genre = genreService.getGenreById(1);
        RatingMPA mpa = mpaService.getRatingMpaById(1);
        Film createdFilm = createFilm(1L, "New Film", "New film description",
                LocalDate.now(), 120, new LinkedHashSet<>(List.of(genre)), mpa);
        filmService.addFilm(createdFilm);
        Genre expectedGenre = null;
        for (Genre g : createdFilm.getGenres()) {
            expectedGenre = g;
            break; // Получаем только первый жанр
        }
        // Проверяем, что созданный фильм имеет ожидаемые значения полей
        assertEquals(createdFilm.getId(), 1L);
        assertEquals(createdFilm.getName(), "New Film");
        assertEquals(createdFilm.getDescription(), "New film description");
        assertEquals(createdFilm.getReleaseDate(), LocalDate.now());
        assertEquals(createdFilm.getDuration(), 120);
        assertEquals(createdFilm.getGenres().size(), 1);
        assertEquals(expectedGenre, genre);
        assertEquals(createdFilm.getMpa(), mpa);
    }

    @Test
    public void updateFilm_shouldReturnUpdatedFilm() {
        // Создаем фильм
        Genre genre = genreService.getGenreById(1);
        RatingMPA mpa = mpaService.getRatingMpaById(1);
        Film createdFilm = createFilm(1L, "New Film", "New film description",
                LocalDate.now(), 120, new LinkedHashSet<>(List.of(genre)), mpa);
        filmService.addFilm(createdFilm);
        System.out.println(filmService.getFilmById(1L).toString());

        //Update film
        Genre genre2 = genreService.getGenreById(2);
        Film updatedFilm = createFilm(1L, "Upd Film", "Upd film description",
                LocalDate.now().minusDays(12), 120, new LinkedHashSet<>(List.of(genre,genre2)), mpa);
        filmService.updateFilm(updatedFilm);

        //Create optional Film object
        Film filmOptional = filmService.getFilmById(1L);

        //Check
        assertEquals(filmOptional.getId(), 1);
        assertEquals(filmOptional.getName(), "Upd Film");
        assertEquals(filmOptional.getGenres().size(), 2);
    }

    @Test
    public void updateFilm_shouldReturnNotFoundException() {
        // UPDATED FILM
        Genre genre = genreService.getGenreById(1);
        RatingMPA mpa = new RatingMPA(1);
        Genre genre2 = genreService.getGenreById(2);
        Film updatedFilm = createFilm(9999L, "Upd Film", "Upd film description",
                LocalDate.now().minusDays(12), 120, new LinkedHashSet<>(List.of(genre)), mpa);

        //Check
        assertThrows(NotFoundException.class, () -> filmService.updateFilm(updatedFilm));
    }

    @Test
    public void getFilm_shouldReturnFilmById() {
        // Создаем фильм
        Genre genre = genreService.getGenreById(1);
        RatingMPA mpa = mpaService.getRatingMpaById(1);
        Film createdFilm = createFilm(1L, "New Film", "New film description",
                LocalDate.now(), 120, new LinkedHashSet<>(List.of(genre)), mpa);
        filmService.addFilm(createdFilm);
        System.out.println(filmService.getFilmById(1L).toString());

        assertEquals(filmService.getFilmById(createdFilm.getId()), createdFilm);
    }

    @Test
    public void getFilm_shouldReturnAllFilmsList() {
        // Создаем фильм
        Genre genre = genreService.getGenreById(1);
        RatingMPA mpa = mpaService.getRatingMpaById(1);
        Film createdFilm = createFilm(1L, "New Film", "New film description",
                LocalDate.now(), 120, new LinkedHashSet<>(List.of(genre)), mpa);
        filmService.addFilm(createdFilm);
        System.out.println(filmService.getFilmById(1L).toString());

        //Second film
        Genre genre2 = genreService.getGenreById(2);
        Film film2 = createFilm(2L, "Upd Film", "Upd film description",
                LocalDate.now().minusDays(12), 120, new LinkedHashSet<>(List.of(genre)), mpa);
        filmService.addFilm(film2);

        List<Film> optionalFilmsList = filmService.getAllFilms();

        assertEquals(optionalFilmsList.size(), 2);
    }

    @Test
    public void addLikeToFilm_shouldReturnFilmWithLike() {
        // Создаем фильм
        Genre genre = genreService.getGenreById(1);
        RatingMPA mpa = mpaService.getRatingMpaById(1);
        Film createdFilm = createFilm(1L, "New Film", "New film description",
                LocalDate.now(), 120, new LinkedHashSet<>(List.of(genre)), mpa);
        filmService.addFilm(createdFilm);
        System.out.println(filmService.getFilmById(1L).toString());

        User user = createUser(1L, "Vladimir", "Foiik",
                LocalDate.now().minusDays(8824), "foiik@yandex.ru");
        userService.createUser(user);

        filmService.addLikeToFilm(createdFilm.getId(), user.getId());

        assertEquals(filmService.getFilmLikes(createdFilm.getId()).size(), 1);
    }

    @Test
    public void removeFilmLike_shouldReturnFilmWithoutLike() {
        // Создаем фильм
        Genre genre = genreService.getGenreById(1);
        RatingMPA mpa = mpaService.getRatingMpaById(1);
        Film createdFilm = createFilm(1L, "New Film", "New film description",
                LocalDate.now(), 120, new LinkedHashSet<>(List.of(genre)), mpa);
        filmService.addFilm(createdFilm);
        System.out.println(filmService.getFilmById(1L).toString());

        //Create user
        User user = createUser(1L, "Vladimir", "Foiik",
                LocalDate.now().minusDays(8824), "foiik@yandex.ru");
        userService.createUser(user);

        //Add like to film
        filmService.addLikeToFilm(createdFilm.getId(), user.getId());

        //Проверка
        assertEquals(filmService.getFilmLikes(createdFilm.getId()).size(), 1);

        //Remove like by user
        filmService.removeLike(createdFilm.getId(), user.getId());

        //Check
        assertEquals(filmService.getFilmLikes(createdFilm.getId()).size(), 0);
    }

    @Test
    public void getPopular_shouldReturnTop1FilmList() {
        // Создаем фильм
        Genre genre = genreService.getGenreById(1);
        RatingMPA mpa = mpaService.getRatingMpaById(1);
        Film createdFilm = createFilm(1L, "New Film", "New film description",
                LocalDate.now(), 120, new LinkedHashSet<>(List.of(genre)), mpa);
        filmService.addFilm(createdFilm);
        System.out.println(filmService.getFilmById(1L).toString());

        assertEquals(filmService.getTopFilms(1L).size(), 1);
        assertEquals(filmService.getTopFilms(1L).get(0), createdFilm);
    }

    @Test
    public void getPopular_shouldReturnTop10FilmList() {
        // Создаем фильм
        Genre genre = genreService.getGenreById(1);
        RatingMPA mpa = mpaService.getRatingMpaById(1);
        Film createdFilm = createFilm(1L, "New Film", "New film description",
                LocalDate.now(), 120, new LinkedHashSet<>(List.of(genre)), mpa);
        filmService.addFilm(createdFilm);
        System.out.println(filmService.getFilmById(1L).toString());
        // Создаем фильм
        Genre genre2 = genreService.getGenreById(2);
        RatingMPA mpa2 = mpaService.getRatingMpaById(2);
        Film createdFilm2 = createFilm(1L, "New Film", "New film description",
                LocalDate.now(), 120, new LinkedHashSet<>(List.of(genre, genre2)), mpa2);
        filmService.addFilm(createdFilm2);

        //Create user
        User user = createUser(1L, "Vladimir", "Foiik",
                LocalDate.now().minusDays(8824), "foiik@yandex.ru");
        userService.createUser(user);

        //Add like to film
        filmService.addLikeToFilm(createdFilm2.getId(), user.getId());

        System.out.println(filmService.getTopFilms(10L));
        assertEquals(filmService.getTopFilms(10L).size(), 2);
        assertEquals(filmService.getTopFilms(10L).get(0), createdFilm2);
    }
}
