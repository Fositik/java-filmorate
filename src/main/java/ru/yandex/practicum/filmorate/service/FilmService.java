package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;

    public Film addFilm(Film film) {
        Film addedFilm = filmStorage.addFilm(film);
        genreStorage.saveGenres(addedFilm);
        return addedFilm;
    }

    public void updateFilm(Film updatedFilm) {
        validateFilmId(updatedFilm.getId());
        Film film = filmStorage.updateFilm(updatedFilm);

        try {
            genreStorage.saveGenres(film);
        } catch (NotFoundException e) {
            log.error("Не удалось получить жанры и рейтинг для фильма с id {}", film.getId(), e);
            throw new FilmNotFoundException("Не удалось получить жанры и рейтинг для фильма с id " + film.getId(), e);
        }
        log.info("Фильм обновлен: {}", film);

    }


    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с указанным ID не найден: " + id));
        LinkedHashSet<Genre> genres = genreStorage.getGenresByFilmId(film.getId());
        film.setGenres(genres); // Заполняем поле 'genres' фильма
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        genreStorage.load(films);
        return films;
    }

    public List<Film> getTopFilms(Long count) {
        List<Film> films = filmStorage.getTopFilms(count);
        genreStorage.load(films);
        return films;
    }

    public void removeFilm(Long id) {
        validateFilmId(id);
        filmStorage.removeFilm(id);
    }

    public void validateFilmId(long filmId) {
        if (filmId <= 0 || !filmStorage.filmExists(filmId)) {
            log.error("Фильм с id {} не найден", filmId);
            throw new NotFoundException("Film with id " + filmId + " not found");
        }
    }
}