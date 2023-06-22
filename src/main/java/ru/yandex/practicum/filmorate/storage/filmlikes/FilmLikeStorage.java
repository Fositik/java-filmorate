package ru.yandex.practicum.filmorate.storage.filmlikes;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

public interface FilmLikeStorage {
    void addLike(long filmId, long userId, int rate) throws NotFoundException;

    void removeLike(long filmId, long userId) throws NotFoundException;

    public boolean isLikedByUser(long filmId, long userId) throws NotFoundException;
}