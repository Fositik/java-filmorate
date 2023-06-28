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

    public static final String DELETE_FILM_GENRES = "DELETE FROM film_genres " +
            "WHERE film_id = ?";
    public static final String INSERT_FILM_GENRES = "INSERT INTO FILM_GENRES " +
            "(FILM_ID, GENRE_ID) " +
            "VALUES (?, ?)";

}