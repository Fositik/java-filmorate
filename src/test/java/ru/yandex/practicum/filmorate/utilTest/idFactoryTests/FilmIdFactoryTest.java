package ru.yandex.practicum.filmorate.utilTest.idFactoryTests;

import org.testng.annotations.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.idfactory.FilmIdFactory;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class FilmIdFactoryTest {
    @Test
    void setIdForUserWhenIdSetIsEmpty() {
        Film film1 = new Film();
        film1.setName("name");
        film1.setDescription("descryption");
        film1.setDuration(120);
        film1.setReleaseDate(LocalDate.now().minusYears(10));

        FilmIdFactory.setUniqueFilmId(film1);

        assertEquals(1, (long) film1.getId());
    }

    @Test
    void setIdForUserWhenIdSetIsNotEmpty() {
        Film film1 = new Film();
        film1.setName("name");
        film1.setDescription("descryption");
        film1.setDuration(120);
        film1.setReleaseDate(LocalDate.now().minusYears(10));
        FilmIdFactory.setUniqueFilmId(film1);

        assertEquals(1, (long) film1.getId());

        Film film2 = new Film();
        film2.setName("name");
        film2.setDescription("descryption");
        film2.setDuration(120);
        film2.setReleaseDate(LocalDate.now().minusYears(10));

        FilmIdFactory.setUniqueFilmId(film2);
        assertEquals(2, (long) film2.getId());
    }
}
