package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmlikes.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmLikeService {
    private final FilmLikeStorage filmLikeStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    public void addLikeToFilm(Long filmId, Long userId) {
        validateFilmIdAndUserId(filmId,userId);
        filmLikeStorage.addLikeToFilm(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
       validateFilmIdAndUserId(filmId, userId);
        filmLikeStorage.removeLike(filmId, userId);
    }

    public Set<Long> getFilmLikes(Long filmId) {
        validateFilmId(filmId);
        return filmLikeStorage.getFilmLikes(filmId);
    }

    private void validateUserId(Long userId) {
        if (!userStorage.userExists(userId)) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    private void validateFilmId(Long filmId) {
        if (!filmStorage.filmExists(filmId)) {
            log.warn("Фильм с id {} не найден", filmId);
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    private void validateFilmIdAndUserId(Long filmId, Long userId) {
        validateFilmId(filmId);
        validateUserId(userId);
    }
}
