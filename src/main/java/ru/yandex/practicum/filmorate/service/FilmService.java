package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
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
public class FilmService {

    private final FilmStorage filmStorage;
    private final Map<Long, Set<Long>> likedFilmsByUser = new HashMap<>();
    public UserService userService;

    //Мапа для хранения фильмов понравившихся пользователю <ID пользователя, Set<ID понравившихся фильмов>>
    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) throws ValidationException, NotFoundException {
        filmStorage.addFilm(film);
        likedFilmsByUser.computeIfAbsent(film.getId(), k -> new HashSet<>()).add(null);
        return film;
    }

    public Film updateFilm(Film updatedFilm) throws ValidationException, NotFoundException {
        filmStorage.updateFilm(updatedFilm);
        return updatedFilm;
    }

    public Film getFilmById(Long id) throws NotFoundException {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Set<Long> getFilmLikes(Long filmId) {
        return likedFilmsByUser.values().stream()
                .filter(likedFilms -> likedFilms != null && likedFilms.contains(filmId))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public List<Film> getTopFilms(Long count) {
        List<Film> popular = likedFilmsByUser.keySet().stream()
                .map(filmStorage::getFilmById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (popular.size() <= 1) {
            return popular;
        } else {
            return popular.stream()
                    .sorted((f1, f2) -> {
                        int likes1 = likedFilmsByUser.getOrDefault(f1.getId(), Collections.emptySet()).size();
                        int likes2 = likedFilmsByUser.getOrDefault(f2.getId(), Collections.emptySet()).size();
                        return Integer.compare(likes2, likes1);
                    })
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }

    public void addLikeToFilm(Long filmId, Long userId) {

        Set<Long> likedBy = likedFilmsByUser.getOrDefault(userId, new HashSet<>());
        FilmValidator.validateLike(likedBy, filmId);

        likedFilmsByUser.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    public void removeLike(Long filmId, Long userId) throws NotFoundException {
        UserValidator.userIncorrectId(userId);

        likedFilmsByUser.computeIfPresent(filmId, (id, likedBy) -> {
            if (likedBy.remove(userId)) {
                return likedBy;
            } else {
                throw new NotFoundException("Пользователь с id " + userId + " не ставил лайк фильму с id " + filmId);
            }
        });

        if (!likedFilmsByUser.containsKey(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
    }
}