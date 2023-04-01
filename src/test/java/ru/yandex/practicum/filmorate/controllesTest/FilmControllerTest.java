package ru.yandex.practicum.filmorate.controllesTest;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        filmController = new FilmController();
    }

    //Тест на создание фильма с корректными данными
    @Test
    public void testAddFilmWithValidData() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(120);

        Film result = filmController.addFilm(film);

        assertEquals("Test Film", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(LocalDate.of(2022, 1, 1), result.getReleaseDate());
        assertEquals(120, result.getDuration());
    }


    //Тест на обновление фильма с корректными данными
    @Test
    public void testUpdateFilmWithValidData() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(120);
        filmController.addFilm(film);
        Film updatedFilm = new Film();
        updatedFilm.setId(film.getId());
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(LocalDate.now());
        updatedFilm.setDuration(150);

        Film result = filmController.addFilm(updatedFilm);

        assertEquals("Updated Film", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(LocalDate.now(), result.getReleaseDate());
        assertEquals(150, result.getDuration());
    }

    //Тест на получение списка всех фильмов
    @Test
    public void testGetAllFilms() {
        Film film1 = new Film();
        film1.setName("Test Film 1");
        film1.setDescription("Test Description 1");
        film1.setReleaseDate(LocalDate.of(2022, 1, 1));
        film1.setDuration(120);
        filmController.addFilm(film1);
        Film film2 = new Film();
        film2.setName("Test Film 2");
        film2.setDescription("Test Description 2");
        film2.setReleaseDate(LocalDate.of(2023, 1, 1));
        film2.setDuration(150);
        filmController.addFilm(film2);

        List<Film> result = filmController.getAllFilms();

        assertEquals(2, result.size());
        assertEquals("Test Film 1", result.get(0).getName());
        assertEquals("Test Description 1", result.get(0).getDescription());
        assertEquals(LocalDate.of(2022, 1, 1), result.get(0).getReleaseDate());
        assertEquals(120, result.get(0).getDuration());
        assertEquals("Test Film 2", result.get(1).getName());
        assertEquals("Test Description 2", result.get(1).getDescription());
        assertEquals(LocalDate.of(2023, 1, 1), result.get(1).getReleaseDate());
        assertEquals(150, result.get(1).getDuration());
    }

    //Тест на создание фильма с некорректными данными
    @Test(expected = ValidationException.class)
    public void testAddFilmWithInvalidData() {
        Film film = new Film();
        film.setName("");
        film.setDescription("");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(0);
        filmController.addFilm(film);
    }

    //Тест на обновление фильма с некорректными данными
    @Test(expected = ValidationException.class)
    public void testUpdateFilmWithInvalidData() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(120);
        filmController.addFilm(film);
        Film updatedFilm = new Film();
        updatedFilm.setId(film.getId());
        updatedFilm.setName("");
        updatedFilm.setDescription("");
        updatedFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        updatedFilm.setDuration(0);
        filmController.updateFilm(updatedFilm);
    }
}