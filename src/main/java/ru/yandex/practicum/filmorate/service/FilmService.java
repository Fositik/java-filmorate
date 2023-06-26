package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmService {
    private final FilmStorage filmStorage;

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public void updateFilm(Film updatedFilm) {
        filmStorage.updateFilm(updatedFilm);
    }


    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с указанным ID не найден: " + id));
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Set<Long> getFilmLikes(Long filmId) {
        return filmStorage.getFilmLikes(filmId);
    }

    public List<Film> getTopFilms(Long count) {
        return filmStorage.getTopFilms(count);
    }


    public void addLikeToFilm(Long filmId, Long userId) {
        filmStorage.addLikeToFilm(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
    }

    public void removeFilm(Long id) {
        filmStorage.removeFilm(id);
    }
}