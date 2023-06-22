package ru.yandex.practicum.filmorate.utilTest.validationTests;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.validators.FilmValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class FilmValidatorTest {
    @Test
    void filmNameIsIncorrectValidateException() {
        Film film1 = new Film();
        film1.setName("");
        film1.setDescription("descryption");
        film1.setDuration(120);
        film1.setReleaseDate(LocalDate.now().minusYears(10));
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> FilmValidator.validateFilm(film1)
        );
        assertEquals("Название не может быть пустым.", ex.getMessage());
    }

    @Test
    void filmDescriptionTooLongValidateException() {
        Film film1 = new Film();
        film1.setName("name");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            sb.append("mi");
        }
        String description = sb.toString();
        film1.setDescription(description);
        film1.setDuration(120);
        film1.setReleaseDate(LocalDate.now().minusYears(10));
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> FilmValidator.validateFilm(film1)
        );
        assertEquals("Максимальная длина описания — 200 символов.", ex.getMessage());
    }

    @Test
    void filmDescriptionIsEmptyValidateException() {
        Film film1 = new Film();
        film1.setName("name");
        film1.setDescription("");
        film1.setDuration(120);
        film1.setReleaseDate(LocalDate.now().minusYears(10));
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> FilmValidator.validateFilm(film1)
        );
        assertEquals("Описание фильма не может быть пустым.", ex.getMessage());
    }

    @Test
    void filmReleaseDateIsIncorrectValidateException() {
        Film film1 = new Film();
        film1.setName("name");
        film1.setDescription("description");
        film1.setDuration(120);
        film1.setReleaseDate(LocalDate.of(1895, 12, 27));
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> FilmValidator.validateFilm(film1)
        );
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года.", ex.getMessage());
    }

    @Test
    void filmDurationIsZeroValidateException() {
        Film film1 = new Film();
        film1.setName("name");
        film1.setDescription("description");
        film1.setDuration(0);
        film1.setReleaseDate(LocalDate.now().minusYears(10));
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> FilmValidator.validateFilm(film1)
        );
        assertEquals("Продолжительность фильма должна быть положительной.", ex.getMessage());
    }

    @Test
    void filmIdAlreadyExistValidateCreateException() {
        Film film1 = new Film();
        film1.setName("name1");
        film1.setDescription("descryption1");
        film1.setDuration(121);
        film1.setReleaseDate(LocalDate.now().minusYears(11));
        film1.setId(1L);

        List<Long> idSet = new ArrayList<>();
        idSet.add(film1.getId());

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> FilmValidator.validateCreate(idSet, film1)
        );
        assertEquals("Такой фильм уже существует", ex.getMessage());
    }

    @Test
    void filmWithIdHasNotBeenCreatedYetValidateUpdateException() {
        Film film1 = new Film();
        film1.setName("name1");
        film1.setDescription("descryption1");
        film1.setDuration(121);
        film1.setReleaseDate(LocalDate.now().minusYears(11));
        film1.setId(1L);

        List<Long> idSet = new ArrayList<>();

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> FilmValidator.validateUpdate(idSet, film1)
        );
        assertEquals("Фильм еще не  был создан", ex.getMessage());
    }

    @Test
    void filmNotFoundException() {
        Film film1 = new Film();
        List<Long> idSet = new ArrayList<>();

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> FilmValidator.validateExist(idSet, film1.getId())
        );
        assertEquals(String.format("Фильм с id %s не найден", film1.getId()), ex.getMessage());
    }
}
