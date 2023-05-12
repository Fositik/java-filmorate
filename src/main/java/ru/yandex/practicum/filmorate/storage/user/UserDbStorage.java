package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendRequest;
import ru.yandex.practicum.filmorate.model.FriendRequestStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.validators.UserValidator;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public User createUser(User newUser) throws ValidationException {
        // Проверяем наличие пользователей в БД, чтобы не создать пользователя с существующим email или login
        String sqlIdList = "SELECT user_id FROM users";
        List<User> existingUsers = jdbcTemplate.query("SELECT * FROM users", new UserRowMapper());
        UserValidator.validateCreate(existingUsers, newUser);

        // Проверяем правильность данных нового пользователя
        UserValidator.validate(newUser);

        // Выполняем SQL-запрос для создания нового пользователя
        String sql = "INSERT INTO users (email, login, user_name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, newUser.getEmail(), newUser.getLogin(), newUser.getName(), newUser.getBirthday());

        // Возвращаем созданного пользователя
        return newUser;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    @Override
    public User getUserById(long id) throws NotFoundException{
        // Проверяем наличие пользователя в БД
        String sqlIdList = "SELECT user_id " +
                "FROM users " +
                "WHERE user_id = ?";
        List<Long> idList = jdbcTemplate.query(sqlIdList, (rs, rowNum) -> rs.getLong("user_id"), id);
        UserValidator.validateExist(idList, id);

        // Получаем пользователя по его идентификатору
        String sql = "SELECT * " +
                "FROM users " +
                "WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new UserRowMapper());
    }

    @Override
    public User updateUser(User updatedUser)  throws ValidationException, NotFoundException {
        //Проверяем пользователя на наличие в БД
        String sqlIdList = "SELECT user_id " +
                "FROM users ";
        List<Long> idList = jdbcTemplate.queryForList(sqlIdList, Long.class);
        UserValidator.validateUpdate(idList, updatedUser);


        String sql = "UPDATE users SET email = ?, login = ?, user_name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql,
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                updatedUser.getName(),
                updatedUser.getBirthday(),
                updatedUser.getId());

        return updatedUser;
    }

    @Override
    public User remove(long id) throws ValidationException, NotFoundException{
        //Проверяем наличие пользователя в БД
        String sqlIdList = "SELECT user_id " +
                "FROM users " +
                "WHERE user_id = ?";
        List<Long> idList = jdbcTemplate.query(sqlIdList, (rs, rowNum) -> rs.getLong("user_id"), id);
        UserValidator.validateExist(idList, id);

        //Выполняем SQL запрос на удаление пользователя из базы данных
        User removedUser = getUserById(id);
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, id);

        //Возвращаем удаленного пользователя
        return removedUser;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sqlCreateFriendship = "INSERT INTO user_friends(" +
                "user_id," +
                "friend_id," +
                "status)" +
                "VALUES (?,?,FALSE)";
        jdbcTemplate.update(sqlCreateFriendship, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sqlRemoveFriendship = "DELETE FROM user_friends " +
                "WHERE user_id = ? " +
                "AND friend_id = ?";
        jdbcTemplate.update(sqlRemoveFriendship, userId, friendId);
    }

    @Override
    public Set<Long> getCommonFriends(long userId, long otherId) {
        String sql = "SELECT uf1.friend_id " +
                "FROM user_friends uf1 " +
                "JOIN user_friends uf2 ON uf1.friend_id = uf2.friend_id " +
                "WHERE uf1.user_id = ? AND uf2.user_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, userId, otherId));
    }

    @Override
    public Set<Long> getFriends(long userId) {
        String sqlGetFriends = "SELECT friend_id " +
                "FROM user_friends " +
                "WHERE user_id = ?";
        List<Long> friendIds = jdbcTemplate.queryForList(sqlGetFriends, Long.class, userId);
        return new HashSet<>(friendIds);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("user_name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            user.setEmail(rs.getString("email"));
            return user;
        }
    }
}

