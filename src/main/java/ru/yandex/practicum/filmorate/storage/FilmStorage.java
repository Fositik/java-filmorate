package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(Long id);

    List<Film> getAllFilms();

    List<Long> addLikeToFilm(Long filmId, Long userId);

    List<Long> removeLike(Long filmId, Long userId);

    List<Long> getFilmLikes(Long filmId);
}