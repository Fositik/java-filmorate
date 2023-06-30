package ru.yandex.practicum.filmorate.storage.film;

public class FilmSQLQueries {
    // Запросы для фильма
    public static final String INSERT_FILM = "INSERT INTO FILMS " +
            "(FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) " +
            "VALUES(? , ? , ? , ? , ?)";
    public static final String UPDATE_FILM = "UPDATE films " +
            "SET film_name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
            "WHERE film_id = ?";
    public static final String SELECT_FILM_BY_ID = "SELECT f.*, r.rating_name " +
            "FROM films f " +
            "JOIN ratings r ON f.rating_id = r.rating_id " +
            "WHERE film_id = ?";
    public static final String SELECT_ALL_FILMS = "SELECT f.*, r.rating_name " +
            "FROM films f " +
            "JOIN ratings r " +
            "ON f.rating_id = r.rating_id ";
    public static final String DELETE_FILM = "DELETE FROM films " +
            "WHERE film_id = ?";
    public static final String FILM_EXISTS = "SELECT COUNT(*) " +
            "FROM films " +
            "WHERE film_id = ?";

    // Запросы для получения топ фильмов
    public static final String SELECT_TOP_FILMS = "SELECT f.*, r.rating_name, COUNT(l.user_id) AS likes_count " +
            "FROM films f " +
            "LEFT JOIN film_user_likes l ON f.film_id = l.film_id " +
            "JOIN ratings r ON f.rating_id = r.rating_id " +
            "GROUP BY f.film_id, r.rating_name " +
            "ORDER BY likes_count DESC NULLS LAST, f.film_id ASC " +
            "LIMIT ?";
}
