package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendRequest;
import ru.yandex.practicum.filmorate.model.FriendRequestStatus;
import ru.yandex.practicum.filmorate.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
@Repository
@Qualifier("FriendRequestDbStorage")
public class FriendRequestDbStorage implements FriendRequestStorage {

    private final DataSource dataSource;
    private final UserDbStorage userStorage;

    public FriendRequestDbStorage(DataSource dataSource, UserDbStorage userStorage) {
        this.dataSource = dataSource;
        this.userStorage = userStorage;
    }

    @Override
    public FriendRequest addFriendRequest(Long senderId, Long receiverId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO friend_requests (" +
                            "sender_id, " +
                            "receiver_id, " +
                            "status) " +
                            "VALUES (?, ?, ?)"
            );
            statement.setLong(1, senderId);
            statement.setLong(2, receiverId);
            statement.setString(3, FriendRequestStatus.PENDING.name());
            statement.executeUpdate();
        }
        return new FriendRequest(senderId, receiverId);
    }

    @Override
    public void acceptFriendRequest(Long friendRequestId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE friend_requests " +
                            "SET status = ? " +
                            "WHERE id = ?"
            );
            statement.setString(1, FriendRequestStatus.ACCEPTED.name());
            statement.setLong(2, friendRequestId);
            statement.executeUpdate();
        }
        FriendRequest friendRequest = getFriendRequestById(friendRequestId);
        userStorage.addFriend(friendRequest.getSenderId(), friendRequest.getReceiverId());
    }

    @Override
    public void rejectFriendRequest(Long friendRequestId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE friend_requests " +
                            "SET status = ? " +
                            "WHERE id = ?"
            );
            statement.setString(1, FriendRequestStatus.REJECTED.name());
            statement.setLong(2, friendRequestId);
            statement.executeUpdate();
        }
    }
    @Override
    public FriendRequest getFriendRequestById(Long friendRequestId) throws SQLException {
        String sql = "SELECT * FROM friend_requests WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, friendRequestId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long senderId = resultSet.getLong("sender_id");
                Long receiverId = resultSet.getLong("receiver_id");
                String status = resultSet.getString("status");
                return new FriendRequest(senderId, receiverId, FriendRequestStatus.valueOf(status));
            } else {
                throw new SQLException("No friend request found with id: " + friendRequestId);
            }
        }
    }
}