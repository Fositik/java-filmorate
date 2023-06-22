package ru.yandex.practicum.filmorate.util.idfactory;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.TreeSet;

public class FilmIdFactory {
    private static TreeSet<Long> filmIds = new TreeSet<>();

    public static Film setUniqueFilmId(Film film) {

        if (filmIds.isEmpty()) {
            filmIds.add(1L);
            film.setId(1L);
        } else {
            final Long lastId = filmIds.last() + 1;
            filmIds.add(lastId);
            film.setId(lastId);
        }
        return film;
    }
}