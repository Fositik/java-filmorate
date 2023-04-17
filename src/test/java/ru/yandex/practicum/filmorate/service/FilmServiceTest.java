package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmServiceTest {

    private FilmStorage filmStorage;
    private FilmService filmService;
    private UserService userService;
    private UserStorage userStorage;

    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage);
    }

    @Test
    void getTopFilms() {
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

        Film film1 = new Film();
        film1.setName("11");
        film1.setDescription("descryption1");
        film1.setDuration(121);
        film1.setReleaseDate(LocalDate.now().minusYears(11));
        filmStorage.addFilm(film1);
        filmService.addLikeToFilm(film1.getId(), user1.getId());

        Film film2 = new Film();
        film2.setName("name2");
        film2.setDescription("descryption2");
        film2.setDuration(120);
        film2.setReleaseDate(LocalDate.now().minusYears(10));
        filmStorage.addFilm(film2);
        filmService.addLikeToFilm(film2.getId(), user1.getId());
        filmService.addLikeToFilm(film2.getId(), user2.getId());

        Film film3 = new Film();
        film3.setName("name3");
        film3.setDescription("descryption3");
        film3.setDuration(130);
        film3.setReleaseDate(LocalDate.now().minusYears(20));
        filmStorage.addFilm(film3);

        List<Film>filmTop = filmService.getTopFilms(10L);

        assertEquals(2,filmTop.size());

        assertEquals(2, filmService.getTopFilms(10L).size());

        Film filmNum0 = filmTop.get(0);
        assertEquals(2,filmNum0.getId());
        assertEquals("name2",filmNum0.getName());
        assertEquals("descryption2", filmNum0.getDescription());
        ///...

        Film filmNum1 = filmTop.get(1);

        assertEquals(1,filmNum1.getId());
        assertEquals("11",filmNum1.getName());
        assertEquals("descryption1", filmNum1.getDescription());
        ///...
    }
}
