package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GenreStorage {
    Optional<Genre> getGenreById(int genreId);

    List<Genre> getAllGenres();

    LinkedHashSet<Genre> getGenresByFilmId(Long filmId);

    void load(List<Film> films);

    void saveGenres(Film film);
}
