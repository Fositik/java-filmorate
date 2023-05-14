package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.CreateUserException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.validators.UserValidator;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Qualifier("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setName(rs.getString("user_name"));
        user.setLogin(rs.getString("login"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    };
    @Override
    public User createUser(User newUser) throws ValidationException, CreateUserException {
        try {
            // Проверяем наличие пользователей в БД, чтобы не создать пользователя с существующим email или login
            String sql = "SELECT * FROM users WHERE email = ? OR login = ?";
            List<User> existingUsers = jdbcTemplate.query(sql, userRowMapper, newUser.getEmail(), newUser.getLogin());

            UserValidator.validateCreate(existingUsers, newUser);

            // Проверяем правильность данных нового пользователя
            UserValidator.validate(newUser);

            // Выполняем SQL-запрос для создания нового пользователя
            sql = "INSERT INTO users (email, user_name, login, birthday) VALUES (?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            String finalSql = sql;
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(finalSql, new String[]{"user_id"});
                stmt.setString(1, newUser.getEmail());
                stmt.setString(2, newUser.getName());
                stmt.setString(3, newUser.getLogin());
                stmt.setDate(4, Date.valueOf(newUser.getBirthday()));
                return stmt;
            }, keyHolder);
            newUser.setId((keyHolder.getKey()).longValue());

            // Возвращаем созданного пользователя
            return newUser;
        } catch (ValidationException e) {
            throw e; // перебросим исключение вверх, чтобы его обработали в другом месте
        } catch (Exception e) {
            // логируем ошибку или обрабатываем исключение в другом месте
            throw new CreateUserException("Ошибка при создании пользователя");
        }
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

//    @Override
//    public User getUserById(long id) throws NotFoundException {
//        // Проверяем наличие пользователя в БД
//        String sqlIdList = "SELECT user_id " +
//                "FROM users " +
//                "WHERE user_id = ?";
//        List<Long> idList = jdbcTemplate.query(sqlIdList, (rs, rowNum) -> rs.getLong("user_id"), id);
//        UserValidator.validateExist(idList, id);
//
//        // Получаем пользователя по его идентификатору
//        String sql = "SELECT * " +
//                "FROM users " +
//                "WHERE user_id = ?";
//        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new UserRowMapper());
//    }
    @Override
    public User getUserById(long id) throws NotFoundException {
        // Проверяем наличие пользователя в БД
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User not found with id: " + id);
        }
    }

    @Override
    public User updateUser(User updatedUser) throws ValidationException, NotFoundException {
        //Проверяем пользователя на наличие в БД
        String sqlIdList = "SELECT user_id " +
                "FROM users ";
        List<Long> idList = jdbcTemplate.queryForList(sqlIdList, Long.class);
        UserValidator.validateUpdate(idList, updatedUser);

        // Выполняем SQL-запрос для обновления пользователя
        String sql = "UPDATE users SET " +
                "email = ?, " +
                "user_name = ?, " +
                "login = ?, " +
                "birthday = ? " +
                "WHERE user_id = ?";

        jdbcTemplate.update(sql,
                updatedUser.getEmail(),
                updatedUser.getName(),
                updatedUser.getLogin(),
                updatedUser.getBirthday(),
                updatedUser.getId());

        return updatedUser;
    }


        @Override
    public User remove(long id) throws ValidationException, NotFoundException {
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

//    void sendFriendRequest(Long senderId, Long receiverId){
//        String sql = "INSERT INTO friend_requests (sender_id, receiver_id) VALUES (?, ?)";
//        jdbcTemplate.update(sql,senderId,receiverId);
//    }
//
//    void confirmFriendRequest(Long request_id){
//        String sql = "UPDATE friend_requests INTO status =TRUE WHERE request_id = ?";
//        jdbcTemplate.update(sql, request_id);
//
//    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO user_friends(user_id, friend_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, "CONFIRMED");
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
        String sql = "SELECT u.user_id FROM users u\n" +
                "JOIN user_friends uf1 ON u.user_id = uf1.friend_id AND uf1.status = 'CONFIRMED' AND uf1.user_id = ?" +
                "JOIN user_friends uf2 ON u.user_id = uf2.friend_id AND uf2.status = 'CONFIRMED' AND uf2.user_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, userId, otherId));
    }

    @Override
    public Set<Long> getFriends(long userId) {
        String sqlGetFriends = "SELECT * FROM user_friends " +
                "WHERE user_id IN " +
                " (SELECT friend_id FROM user_friends WHERE user_id = ? AND status = 'CONFIRMED')";
        List<Long> friendIds = jdbcTemplate.queryForList(sqlGetFriends, Long.class, userId);
        return new HashSet<>(friendIds);
    }
//    private static class UserRowMapper implements RowMapper<User> {
//        @Override
//        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
//            User user = new User();
//            user.setId(rs.getLong("user_id"));
//            user.setLogin(rs.getString("login"));
//            user.setName(rs.getString("user_name"));
//            user.setBirthday(rs.getDate("birthday").toLocalDate());
//            user.setEmail(rs.getString("email"));
//            return user;
//        }
//    }

}

