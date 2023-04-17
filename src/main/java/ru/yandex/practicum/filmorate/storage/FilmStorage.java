package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(Long id);

    List<Film> getAllFilms();

    List<Long> addLikeToFilm(Long filmId, Long userId);

    List<Long> removeLike(Long filmId, Long userId);

    Set<Long> getFilmLikes(Long filmId);

    List<Film> getTopFilms(Long count);
}