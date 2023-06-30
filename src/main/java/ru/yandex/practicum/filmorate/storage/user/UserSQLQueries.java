package ru.yandex.practicum.filmorate.storage.user;

public class UserSQLQueries {
    public static final String INSERT_USER = "INSERT INTO users " +
            "(user_id, email, user_name, login, birthday) " +
            "VALUES (DEFAULT, ?, ?, ?, ?)";
    public static final String SELECT_ALL_USERS = "SELECT * " +
            "FROM users";
    public static final String SELECT_USER_BY_ID = "SELECT * " +
            "FROM users " +
            "WHERE user_id = ?";
    public static final String UPDATE_USER = "UPDATE users " +
            "SET email = ?, user_name = ?, login = ?, birthday = ? " +
            "WHERE user_id = ?";
    public static final String DELETE_USER = "DELETE FROM users " +
            "WHERE user_id = ?";
    public static final String USER_EXISTS = "SELECT COUNT(*) FROM users " +
            "WHERE user_id = ?";
}
