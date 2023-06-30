package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> getFilmById(Long id);

    List<Film> getAllFilms();


    List<Film> getTopFilms(Long count);

    void removeFilm(Long id);

    boolean filmExists(long filmId);
}