package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.filmlikes.FilmLikeStorage;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmLikeService {
    private final FilmLikeStorage filmLikeStorage;
    private final FilmService filmService;
    private final UserService userService;

    public void addLikeToFilm(Long filmId, Long userId) {
        filmService.validateFilmId(filmId);
        userService.validateUserId(userId);
        filmLikeStorage.addLikeToFilm(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmService.validateFilmId(filmId);
        userService.validateUserId(userId);
        filmLikeStorage.removeLike(filmId, userId);
    }

    public Set<Long> getFilmLikes(Long filmId) {
        filmService.validateFilmId(filmId);
        return filmLikeStorage.getFilmLikes(filmId);
    }
}
