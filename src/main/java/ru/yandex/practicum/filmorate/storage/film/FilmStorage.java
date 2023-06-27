package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> getFilmById(Long id);

    List<Film> getAllFilms();

    void addLikeToFilm(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Set<Long> getFilmLikes(Long filmId);

    List<Film> getTopFilms(Long count);

    void removeFilm(Long id);

    boolean filmExists (long filmId);
}