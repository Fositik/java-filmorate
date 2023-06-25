package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Qualifier("UserDbStorage")
@RequiredArgsConstructor  //генерирует конструктор для всех полей класса, помеченных final или @NonNull
@Slf4j
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
    public User createUser(User newUser) {
        try {
            // Выполняем SQL-запрос для создания нового пользователя
            String sql = "INSERT INTO users (user_id, email, user_name, login, birthday) VALUES (DEFAULT, ?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
                stmt.setString(1, newUser.getEmail());
                stmt.setString(2, newUser.getName());
                stmt.setString(3, newUser.getLogin());
                stmt.setDate(4, Date.valueOf(newUser.getBirthday()));
                return stmt;
            }, keyHolder);
            Number key = keyHolder.getKey();
            if (key == null) {
                log.error("Ошибка при создании пользователя {}: не удалось получить id", newUser);
                throw new CreateUserException("Ошибка при создании пользователя: не удалось получить id");
            }
            newUser.setId(key.longValue());
            log.info("User {} was created", newUser);
            // Возвращаем созданного пользователя
            return newUser;
        } catch (ValidationException e) {
            throw e; // перебросим исключение вверх, чтобы его обработали в другом месте
        } catch (Exception e) {
            throw new CreateUserException("Ошибка при создании пользователя");
        }
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public User getUserById(long id) {
        // Проверяем наличие пользователя в БД
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            log.info("Пользователь найден, id: {}", id);
            return jdbcTemplate.queryForObject(sql, userRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь не найден, id: " + id);
        }
    }

    @Override
    public User updateUser(User updatedUser) {
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
        log.info("Пользователь обновлен: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public User remove(long id) {
        //Выполняем SQL запрос на удаление пользователя из базы данных
        User removedUser = getUserById(id);
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Пользователь удален, id: {}", id);
        //Возвращаем удаленного пользователя
        return removedUser;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO user_friends(friendship_id, user_id, friend_id, status) VALUES (DEFAULT, ?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, "CONFIRMED");
        log.info("Пользователь id: {},добавлен в друзбя пользователю id: {}", userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sqlRemoveFriendship = "DELETE FROM user_friends " +
                "WHERE user_id = ? " +
                "AND friend_id = ?";
        jdbcTemplate.update(sqlRemoveFriendship, userId, friendId);
        log.info("Пользователь id: {},удален из друзей пользователя id: {}", userId, friendId);

    }

    @Override
    public Set<Long> getCommonFriends(long userId, long otherId) {
        String sql = "SELECT u.user_id FROM users u\n" +
                "JOIN user_friends uf1 ON u.user_id = uf1.friend_id AND uf1.status = 'CONFIRMED' AND uf1.user_id = ?" +
                "JOIN user_friends uf2 ON u.user_id = uf2.friend_id AND uf2.status = 'CONFIRMED' AND uf2.user_id = ?";
        log.info("Получение общих друзей пользовтелей с id {} и {}", userId, otherId);
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, userId, otherId));
    }

    @Override
    public Set<Long> getFriends(long userId) {
        String sqlGetFriends = "SELECT friend_id FROM user_friends WHERE user_id = ? AND status = 'CONFIRMED'";
        List<Long> friendIds = jdbcTemplate.queryForList(sqlGetFriends, Long.class, userId);
        log.info("Получение списка друзей пользовтеля с id: {}", userId);
        return new HashSet<>(friendIds);
    }
}

