package ru.yandex.practicum.filmorate.storage.friend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userRowMapper;

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbcTemplate.update(FriendSQLQueries.INSERT_FRIEND, userId, friendId, "CONFIRMED");
        log.info("Пользователь id: {}, добавлен в друзья пользователю id: {}", userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        jdbcTemplate.update(FriendSQLQueries.DELETE_FRIEND, userId, friendId);
        log.info("Пользователь id: {}, удален из друзей пользователя id: {}", userId, friendId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        log.info("Получение общих друзей пользователей с id {} и {}", userId, otherId);
        return jdbcTemplate.query(FriendSQLQueries.SELECT_COMMON_FRIENDS, new Object[]{userId, otherId}, userRowMapper);
    }

    @Override
    public List<User> getFriends(long userId) {
        List<User> friends = jdbcTemplate.query(FriendSQLQueries.SELECT_GET_FRIENDS,
                new Object[]{userId}, userRowMapper);
        log.info("Получение списка друзей пользователя с id: {}", userId);
        return friends;
    }
}
