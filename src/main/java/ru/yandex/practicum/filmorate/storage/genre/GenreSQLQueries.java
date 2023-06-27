package ru.yandex.practicum.filmorate.storage.genre;

public class GenreSQLQueries {
    public static final String SELECT_GENRE_BY_ID = "SELECT * " +
            "FROM genres " +
            "WHERE genre_id = ?";
    public static final String SELECT_ALL_GENRES = "SELECT * " +
            "FROM genres";
    public static final String SELECT_GENRE_BY_FILM_ID = "SELECT g.* " +
            "FROM genres g " +
            "INNER JOIN film_genres fg " +
            "ON g.genre_id = fg.genre_id " +
            "WHERE fg.film_id = ?";

    public static final String SELECT_GENRES_IN_FILM = "SELECT fg.film_id, g.genre_id, g.genre_name " +
            "FROM film_genres fg " +
            "INNER JOIN genres g " +
            "ON fg.genre_id = g.genre_id " +
            "WHERE fg.film_id " +
            "IN (:filmIds)";
}