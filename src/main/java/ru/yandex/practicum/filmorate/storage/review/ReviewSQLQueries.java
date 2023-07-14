package ru.yandex.practicum.filmorate.storage.review;

public class ReviewSQLQueries {
    public static final String CREATE_REVIEW = "INSERT INTO reviews " +
            "(content, is_positive, user_id, film_id, useful_count) " +
            "VALUES (?, ?, ?, ?, ?);";
    public static final String UPDATE_REVIEW = "UPDATE reviews " +
            "SET content = ?, is_positive = ? " +
            "WHERE review_id =?;";
    public static final String REMOVE_REVIEW = "DELETE FROM reviews " +
            "WHERE review_id = ?";
    public static final String GET_REVIEW_BY_ID = "SELECT * " +
            "FROM reviews " +
            "WHERE review_id = ?";

    public static final String GET_ALL_REVIEWS = "SELECT * " +
            "FROM reviews " +
            "ORDER BY useful_count DESC " +
            "LIMIT ?";
    public static final String GET_ALL_REVIEWS_BY_FILM_ID = "SELECT * " +
            "FROM reviews " +
            "WHERE film_id = ? " +
            "ORDER BY useful_count DESC " +
            "LIMIT ?";
    public static final String LIKE_REVIEW = "MERGE INTO review_reactions " +
            "(user_id, review_id, reaction) " +
            "KEY (user_id, review_id) " +
            "VALUES (?, ?, 'LIKE')";
    public static final String DISLIKE_REVIEW = "MERGE INTO review_reactions " +
            "(user_id, review_id, reaction) " +
            "KEY (user_id, review_id) " +
            "VALUES (?, ?, 'DISLIKE')";
    public static final String REMOVE_LIKE = "DELETE FROM review_reactions " +
            "WHERE user_id = ? " +
            "AND review_id = ?";
    public static final String REMOVE_DISLIKE = REMOVE_LIKE;
    public static final String UPDATE_REVIEW_INCREMENT_USEFUL_COUNT = "UPDATE reviews " +
            "SET useful_count = useful_count + 1 " +
            "WHERE review_id = ?";
    public static final String UPDATE_REVIEW_DECREMENT_USEFUL_COUNT = "UPDATE reviews " +
            "SET useful_count = useful_count - 1 " +
            "WHERE review_id = ?";
    public static final String CHECK_REVIEW_EXIST = "SELECT COUNT(*) " +
            "FROM REVIEWS " +
            "WHERE REVIEW_ID = ?";
}
