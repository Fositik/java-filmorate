package ru.yandex.practicum.filmorate.storage.friend;

public class FriendSQLQueries {
    public static final String INSERT_FRIEND = "INSERT INTO user_friends" +
            "(user_id, friend_id, status) " +
            "VALUES (?, ?, ?)";
    public static final String DELETE_FRIEND = "DELETE FROM user_friends " +
            "WHERE user_id = ? " +
            "AND friend_id = ?";
    public static final String SELECT_COMMON_FRIENDS = "SELECT u.* " +
            "FROM users u " +
            "JOIN user_friends uf1 " +
            "ON u.user_id = uf1.friend_id " +
            "AND uf1.status = 'CONFIRMED' " +
            "AND uf1.user_id = ? " +
            "JOIN user_friends uf2 " +
            "ON u.user_id = uf2.friend_id " +
            "AND uf2.status = 'CONFIRMED' " +
            "AND uf2.user_id = ?";
}
