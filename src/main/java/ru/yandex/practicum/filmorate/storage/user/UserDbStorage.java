package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.CreateUserException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
     private final UserMapper userRowMapper;

    @Override
    public User createUser(User newUser) {
        try {
            String sql = UserSQLQueries.INSERT_USER;

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
            return newUser;
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при создании пользователя {}: " +
                    "пользователь с таким логином или email уже существует", newUser, e);
            throw new CreateUserException("Пользователь с таким логином или email уже существует");
        } catch (DataAccessException e) {
            throw new CreateUserException("Ошибка при создании пользователя");
        }
    }

    @Override
    public List<User> getAllUsers() {
        String sql = UserSQLQueries.SELECT_ALL_USERS;
        log.info("получение списка всех пользователей");
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> getUserById(long id) {
        String sql = UserSQLQueries.SELECT_USER_BY_ID;
        try {
            log.info("Пользователь найден, id: {}", id);
            User result = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.of(result);
        } catch (EmptyResultDataAccessException e) {
            log.error("пользователь с id: {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public User updateUser(User updatedUser) {
        String sql = UserSQLQueries.UPDATE_USER;

        int affectedRows = jdbcTemplate.update(sql,
                updatedUser.getEmail(),
                updatedUser.getName(),
                updatedUser.getLogin(),
                updatedUser.getBirthday(),
                updatedUser.getId());

        if (affectedRows == 0) {
            log.warn("Попытка обновить пользователя с id: {}. Пользователь не найден", updatedUser.getId());
            throw new NotFoundException("Пользователь с указанным ID не найден: " + updatedUser.getId());
        } else {
            log.info("Пользователь обновлен: {}", updatedUser);
            return updatedUser;
        }
    }


    @Override
    public void remove(long id) {
        String sql = UserSQLQueries.DELETE_USER;
        int affectedRows = jdbcTemplate.update(sql, id);
        if (affectedRows == 0) {
            log.warn("Попытка удалить пользователя с id: {}. Пользователь не найден", id);
            throw new NotFoundException("Пользователь с указанным ID не найден: " + id);
        } else {
            log.info("Пользователь удален, id: {}", id);
        }
    }

    public boolean userExists(Long userId) {
        String sql = UserSQLQueries.USER_EXISTS;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }
}

