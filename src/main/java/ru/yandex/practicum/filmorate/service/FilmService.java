package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.util.validators.FilmValidator;
import ru.yandex.practicum.filmorate.util.validators.UserValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Film addFilm(Film film) throws ValidationException, NotFoundException {
        List<Film> filmList = getAllFilms();
        List<Long> filmIds = filmList.stream().map(Film::getId).collect(Collectors.toList());

        FilmValidator.validateFilm(film);

        Film createdFilm = filmStorage.addFilm(film);

        FilmValidator.validateCreate(filmIds, createdFilm);
        return createdFilm;
    }

    public void updateFilm(Film updatedFilm) throws ValidationException, NotFoundException {
        List<Film> filmList = getAllFilms();
        List<Long> filmIds = filmList.stream().map(Film::getId).collect(Collectors.toList());

        FilmValidator.validateExist(filmIds, updatedFilm.getId());
        FilmValidator.validateUpdate(filmIds, updatedFilm);

        filmStorage.updateFilm(updatedFilm);
    }


    public Film getFilmById(Long id) {
        List<Film> filmList = getAllFilms();
        List<Long> filmIds = filmList.stream().map(Film::getId).collect(Collectors.toList());

        FilmValidator.validateExist(filmIds, id);
        return filmStorage.getFilmById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Set<Long> getFilmLikes(Long filmId) {
        return filmStorage.getFilmLikes(filmId);
    }

    public List<Film> getTopFilms(Long count) {
        log.info("Получение списка из топ {} фильмов", count);

        return filmStorage.getTopFilms(count);
    }


    public void addLikeToFilm(Long filmId, Long userId) {
        Set<Long> likedBy = getFilmLikes(filmId);
        FilmValidator.validateLike(likedBy, filmId);
        filmStorage.addLikeToFilm(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) throws NotFoundException {
        UserValidator.userIncorrectId(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public void removeFilm(Long id) throws NotFoundException {
        List<Film> filmList = getAllFilms();
        List<Long> filmIds = filmList.stream().map(Film::getId).collect(Collectors.toList());

        FilmValidator.validateExist(filmIds, id);

        filmStorage.removeFilm(id);
    }
}