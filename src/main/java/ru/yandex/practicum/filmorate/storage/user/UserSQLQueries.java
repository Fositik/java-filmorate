package ru.yandex.practicum.filmorate.storage.user;

public class UserSQLQueries {
    // Запросы для пользователя
    public static final String INSERT_USER = "INSERT INTO users (user_id, email, user_name, login, birthday) VALUES (DEFAULT, ?, ?, ?, ?)";
    public static final String SELECT_ALL_USERS = "SELECT * FROM users";
    public static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    public static final String UPDATE_USER = "UPDATE users SET email = ?, user_name = ?, login = ?, birthday = ? WHERE user_id = ?";
    public static final String DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    public static final String USER_EXISTS = "SELECT COUNT(*) FROM users WHERE user_id = ?";

    // Запросы для друзей
    public static final String INSERT_FRIEND = "INSERT INTO user_friends(user_id, friend_id, status) VALUES (?, ?, ?)";
    public static final String DELETE_FRIEND = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
    public static final String SELECT_COMMON_FRIENDS = "SELECT u.* FROM users u JOIN user_friends uf1 ON u.user_id = uf1.friend_id AND uf1.status = 'CONFIRMED' AND uf1.user_id = ? JOIN user_friends uf2 ON u.user_id = uf2.friend_id AND uf2.status = 'CONFIRMED' AND uf2.user_id = ?";
    public static final String SELECT_FRIENDS = "SELECT friend_id FROM user_friends WHERE user_id = ? AND status = 'CONFIRMED'";
}
