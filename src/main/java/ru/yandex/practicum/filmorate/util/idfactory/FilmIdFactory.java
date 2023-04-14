package ru.yandex.practicum.filmorate.util.idfactory;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.TreeSet;

public class FilmIdFactory {
    public static void setUniqueFilmId(List<Long> idList, Film film) {
        TreeSet<Long> filmIds = new TreeSet<>(idList);
        if (filmIds.isEmpty()) {
            film.setId(1L);
            return;
        }
        final Long lastId = filmIds.last() + 1;
        film.setId(lastId);
    }

}