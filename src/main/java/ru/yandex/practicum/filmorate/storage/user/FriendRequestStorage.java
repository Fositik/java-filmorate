package ru.yandex.practicum.filmorate.storage.user;


    import ru.yandex.practicum.filmorate.model.FriendRequest;

    import java.sql.SQLException;

public interface FriendRequestStorage {

    FriendRequest addFriendRequest(Long senderId, Long receiverId) throws SQLException;

    void acceptFriendRequest(Long friendRequestId) throws SQLException;

    void rejectFriendRequest(Long friendRequestId) throws SQLException;

    FriendRequest getFriendRequestById(Long friendRequestId) throws SQLException;

}