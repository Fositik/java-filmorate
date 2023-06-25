package ru.yandex.practicum.filmorate.storage.filmlikes;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

public interface FilmLikeStorage {
    void addLike(long filmId, long userId, int rate);

    void removeLike(long filmId, long userId);

    public boolean isLikedByUser(long filmId, long userId);
}