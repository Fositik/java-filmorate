package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.ratingmpa.RatingMpaStorage;
import ru.yandex.practicum.filmorate.util.validators.FilmValidator;
import ru.yandex.practicum.filmorate.util.validators.UserValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final Map<Long, Set<Long>> likedFilmsByUser = new HashMap<>();
    private UserService userService;
    private GenreStorage genreStorage;
    private RatingMpaStorage ratingMpaStorage;
    private List<Genre> genres; // новое поле в классе Film

    //Мапа для хранения фильмов понравившихся пользователю <ID пользователя, Set<ID понравившихся фильмов>>
    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) throws ValidationException, NotFoundException {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film updatedFilm) throws ValidationException, NotFoundException {
        List<Film> filmList = getAllFilms();
        List<Long> filmIds = filmList.stream().map(Film::getId).collect(Collectors.toList());

        FilmValidator.validateExist(filmIds, updatedFilm.getId());
        FilmValidator.validateUpdate(filmIds, updatedFilm);

        filmStorage.updateFilm(updatedFilm);
        return updatedFilm;
    }


    public Film getFilmById(Long id) {
        List<Film> filmList = getAllFilms();
        List<Long> filmIds = filmList.stream().map(Film::getId).collect(Collectors.toList());

        FilmValidator.validateExist(filmIds, id);
        return filmStorage.getFilmById(id);
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        return films;
    }

    public Set<Long> getFilmLikes(Long filmId) {
      return  filmStorage.getFilmLikes(filmId);
    }

    public List<Film> getTopFilms(Long count) {
        log.info("Получение списка из топ {} фильмов", count);

        return filmStorage.getTopFilms(count);
    }


    public void addLikeToFilm(Long filmId, Long userId) {

        Set<Long> likedBy = likedFilmsByUser.getOrDefault(userId, new HashSet<>());
        FilmValidator.validateLike(likedBy, filmId);
        filmStorage.addLikeToFilm(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) throws NotFoundException {
        UserValidator.userIncorrectId(userId);
        filmStorage.removeLike(filmId, userId);
    }
}