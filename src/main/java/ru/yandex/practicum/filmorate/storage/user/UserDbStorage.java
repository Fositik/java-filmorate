package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
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
            String sql = UserSQLQueries.INSERT_USER;

            KeyHolder keyHolder = new GeneratedKeyHolder();

            // Проверяем и обновляем поле name, если оно пустое или null
            if (newUser.getName() == null || newUser.getName().isEmpty() || newUser.getName().isBlank()) {
                newUser.setName(newUser.getLogin());
                log.info("Поле 'name' не может быть пустым, оно будет эквивалентно полю 'login'");
            }

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
            //Так как мы избавились от класса валидации с целью повышения производительности приложения,
            // добавим проверку уникальности на уровне базы данных
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при создании пользователя {}: " +
                    "пользователь с таким логином или email уже существует", newUser, e);
            throw new CreateUserException("Пользователь с таким логином или email уже существует");
        } catch (ValidationException e) {
            throw e; // перебросим исключение вверх, чтобы его обработали в другом месте
        } catch (Exception e) {
            throw new CreateUserException("Ошибка при создании пользователя");
        }
    }

    @Override
    public List<User> getAllUsers() {
        String sql = UserSQLQueries.SELECT_ALL_USERS;
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> getUserById(long id) {
        // Проверяем наличие пользователя в БД
        String sql = UserSQLQueries.SELECT_USER_BY_ID;
        try {
            log.info("Пользователь найден, id: {}", id);
            User result = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            log.error("пользователь с id: {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public User updateUser(User updatedUser) {
        userExists(updatedUser.getId());
        // Выполняем SQL-запрос для обновления пользователя
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
        // Выполняем SQL запрос на удаление пользователя из базы данных
        userExists(id);
        String sql = UserSQLQueries.DELETE_USER;
        int affectedRows = jdbcTemplate.update(sql, id);
        if (affectedRows == 0) {
            log.warn("Попытка удалить пользователя с id: {}. Пользователь не найден", id);
            throw new NotFoundException("Пользователь с указанным ID не найден: " + id);
        } else {
            log.info("Пользователь удален, id: {}", id);
        }
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        // Проверяем, существуют ли пользователи в базе данных
        userExists(userId);
        userExists(friendId);

        String sql = UserSQLQueries.INSERT_FRIEND;
        jdbcTemplate.update(sql, userId, friendId, "CONFIRMED");
        log.info("Пользователь id: {}, добавлен в друзья пользователю id: {}", userId, friendId);
    }


    @Override
    public void removeFriend(long userId, long friendId) {
        userExists(userId);
        userExists(friendId);
        String sqlRemoveFriendship = UserSQLQueries.DELETE_FRIEND;
        jdbcTemplate.update(sqlRemoveFriendship, userId, friendId);
        log.info("Пользователь id: {},удален из друзей пользователя id: {}", userId, friendId);

    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        userExists(userId);
        userExists(otherId);
        String sql = UserSQLQueries.SELECT_COMMON_FRIENDS;
        log.info("Получение общих друзей пользовтелей с id {} и {}", userId, otherId);
        return jdbcTemplate.query(sql, new Object[]{userId, otherId}, userRowMapper);
    }

    @Override
    public Set<Long> getFriends(long userId) {
        String sqlGetFriends = UserSQLQueries.SELECT_FRIENDS;
        List<Long> friendIds = jdbcTemplate.queryForList(sqlGetFriends, Long.class, userId);
        log.info("Получение списка друзей пользовтеля с id: {}", userId);
        return new HashSet<>(friendIds);
    }

    public boolean userExists(Long userId) {
        String sql = UserSQLQueries.USER_EXISTS;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        if (count == null || count == 0) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        return true;
    }
}

