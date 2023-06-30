package ru.yandex.practicum.filmorate.storage.ratingmpa;

public class RatingMpaSQLQueries {
    public static final String SELECT_RATING_BY_ID = "SELECT * " +
            "FROM ratings " +
            "WHERE rating_id = ?";
    public static final String SELECT_ALL_RATINGS = "SELECT * " +
            "FROM ratings";
}
