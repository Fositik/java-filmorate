package ru.yandex.practicum.filmorate.storage.filmlikes;

public class FilmLikeSQLQueries {
    public static final String INSERT_FILM_USER_LIKES = "INSERT INTO film_user_likes " +
            "(film_id, user_id) " +
            "VALUES (?, ?)";
    public static final String DELETE_FILM_USER_LIKES = "DELETE FROM film_user_likes " +
            "WHERE film_id = ? " +
            "AND user_id = ?";
    public static final String SELECT_FILM_LIKES = "SELECT user_id " +
            "FROM film_user_likes " +
            "WHERE film_id = ?";

}