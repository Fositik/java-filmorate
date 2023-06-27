package ru.yandex.practicum.filmorate.storage.friend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userRowMapper;

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = FriendSQLQueries.INSERT_FRIEND;
        jdbcTemplate.update(sql, userId, friendId, "CONFIRMED");
        log.info("Пользователь id: {}, добавлен в друзья пользователю id: {}", userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sqlRemoveFriendship = FriendSQLQueries.DELETE_FRIEND;
        jdbcTemplate.update(sqlRemoveFriendship, userId, friendId);
        log.info("Пользователь id: {}, удален из друзей пользователя id: {}", userId, friendId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        String sql = FriendSQLQueries.SELECT_COMMON_FRIENDS;
        log.info("Получение общих друзей пользовтелей с id {} и {}", userId, otherId);
        return jdbcTemplate.query(sql, new Object[]{userId, otherId}, userRowMapper);
    }

    @Override
    public Set<Long> getFriends(long userId) {
        String sqlGetFriends = FriendSQLQueries.SELECT_FRIENDS;
        List<Long> friendIds = jdbcTemplate.queryForList(sqlGetFriends, Long.class, userId);
        log.info("Получение списка друзей пользовтеля с id: {}", userId);
        return new HashSet<>(friendIds);
    }
}
