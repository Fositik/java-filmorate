package ru.yandex.practicum.filmorate.storage.filmlikes;

import java.util.Set;

public interface FilmLikeStorage {

    void removeLike(Long filmId, Long userId);

    Set<Long> getFilmLikes(Long filmId);

    void addLikeToFilm(Long filmId, Long userId);
}