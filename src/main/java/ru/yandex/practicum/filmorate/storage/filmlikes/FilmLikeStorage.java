package ru.yandex.practicum.filmorate.storage.filmlikes;

public interface FilmLikeStorage {
    void addLike(long filmId, long userId, int rate);

    void removeLike(long filmId, long userId);

    boolean isLikedByUser(long filmId, long userId);
}