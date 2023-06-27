package ru.yandex.practicum.filmorate.storage.film;

public class FilmSQLQueries {
    // Запросы для фильма
    public static final String INSERT_FILM = "INSERT INTO FILMS " +
            "(FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) " +
            "VALUES(? , ? , ? , ? , ?)";
    public static final String UPDATE_FILM = "UPDATE films " +
            "SET film_name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
            "WHERE film_id = ?";
    public static final String SELECT_FILM_BY_ID = "SELECT * " +
            "FROM films " +
            "WHERE film_id = ?";
    public static final String SELECT_ALL_FILMS = "SELECT * " +
            "FROM films";
    public static final String DELETE_FILM = "DELETE FROM films " +
            "WHERE film_id = ?";
    public static final String FILM_EXISTS = "SELECT COUNT(*) " +
            "FROM films " +
            "WHERE film_id = ?";

    // Запросы для жанров
    public static final String DELETE_FILM_GENRES = "DELETE FROM film_genres " +
            "WHERE film_id = ?";
    public static final String INSERT_FILM_GENRES = "INSERT INTO FILM_GENRES " +
            "(FILM_ID, GENRE_ID) " +
            "VALUES (?, ?)";

    // Запросы для получения топ фильмов
    public static final String SELECT_TOP_FILMS = "SELECT f.*, COUNT(l.user_id) AS likes_count " +
            "FROM films f " +
            "LEFT JOIN film_user_likes l ON f.film_id = l.film_id " +
            "GROUP BY f.film_id " +
            "ORDER BY likes_count DESC NULLS LAST, f.film_id ASC " +
            "LIMIT ?";
}
