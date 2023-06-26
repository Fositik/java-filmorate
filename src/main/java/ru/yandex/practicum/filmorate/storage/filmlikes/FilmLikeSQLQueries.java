package ru.yandex.practicum.filmorate.storage.filmlikes;

public class FilmLikeSQLQueries {
    public static final String INSERT_LIKE = "INSERT INTO film_user_likes (film_id, user_id) VALUES (?, ?)";
    public static final String REMOVE_LIKE = "DELETE FROM film_user_likes WHERE film_id = ? AND user_id = ?";
    public static final String IS_LIKED_BY_USER = "SELECT COUNT(*) FROM film_user_likes WHERE film_id = ? AND user_id = ?";
}