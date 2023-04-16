package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.util.validators.FilmValidator;
import ru.yandex.practicum.filmorate.util.validators.UserValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final Map<Long, Set<Long>> likedFilmsByUser;
    public UserService userService;

    //Мапа для хранения фильмов понравившихся пользователю <ID пользователя, Set<ID понравившихся фильмов>>
    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        this.likedFilmsByUser = new HashMap<>();
    }

    public Film addFilm(Film film) throws ValidationException, NotFoundException {
        filmStorage.addFilm(film);
        return film;
    }

    public Film updateFilm(Film updatedFilm) throws ValidationException, NotFoundException {
        filmStorage.updateFilm(updatedFilm);
        return updatedFilm;
    }

    public Film getFilmById(Long id) throws NotFoundException {
        Film film = filmStorage.getFilmById(id);
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> filmList = filmStorage.getAllFilms();
        return filmList;
    }

    public List<Film> getTopFilmsByLikes(Long count) {
        List<Film> popular = filmStorage.getAllFilms();
        if (popular.size() <= 1) {
            return popular;
        } else {
            return popular.stream()
                    .sorted((film1, film2) -> {
                        int result = Integer.compare(
                                filmStorage.getFilmLikes(film1.getId()).size(),
                                filmStorage.getFilmLikes(film2.getId()).size()
                        );
                        result = -1 * result;
                        return result;
                    })
                    .limit(count)
                    .collect(Collectors.toList());
        }

    }

    public Set<Long> addLikeToFilm(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        Set<Long> likedBy = likedFilmsByUser.getOrDefault(userId, new HashSet<>());
        FilmValidator.validateLike(likedBy, filmId);
        likedBy.add(userId);
        // filmStorage.updateFilm(film);
        return likedBy;
    }

    public Set<Long> removeLike(Long filmId, Long userId) throws NotFoundException {
        Film film = filmStorage.getFilmById(filmId);
        Set<Long> likedBy = likedFilmsByUser.getOrDefault(userId, new HashSet<>());
        List<Long> likedByIdsList = new ArrayList<>(likedBy);
        UserValidator.validateExist(likedByIdsList, filmId);
        filmStorage.removeLike(filmId, userId);
        likedBy.remove(userId);
        return likedBy;
    }
}