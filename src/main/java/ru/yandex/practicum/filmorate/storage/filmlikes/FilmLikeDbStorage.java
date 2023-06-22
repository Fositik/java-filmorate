package ru.yandex.practicum.filmorate.storage.filmlikes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.Optional;

@Repository
@Slf4j
public class FilmLikeDbStorage implements FilmLikeStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmLikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(long filmId, long userId, int rate) throws NotFoundException {
        String sql = "INSERT INTO film_user_likes (film_id, user_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, filmId, userId);
        } catch (Exception e) {
            log.error("Ошибка при добавлении лайка к фильму с id: {} пользователем с id: {}", filmId, userId, e);
            throw new NotFoundException("Фильм или пользователь не найдены");
        }
    }

    @Override
    public void removeLike(long filmId, long userId) throws NotFoundException {
        String sql = "DELETE FROM film_user_likes WHERE film_id = ? AND user_id = ?";
        try {
            int deletedRows = jdbcTemplate.update(sql, filmId, userId);
            if (deletedRows == 0) {
                throw new NotFoundException(String.format(
                        "Лайк от пользователя %d к фильму %d не найден", userId, filmId)
                );
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении лайка с фильма под id {} пользователем с id {}", filmId, userId, e);
            throw new NotFoundException("Фильм или пользователь не найдены");
        }
    }

    @Override
    public boolean isLikedByUser(long filmId, long userId) throws NotFoundException {
        String sql = "SELECT COUNT(*) FROM film_user_likes WHERE film_id = ? AND user_id = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
            return Optional.ofNullable(count).orElse(0) > 0;
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка при проверке лайка к фильму под id {} пользователем с id {}", filmId, userId, e);
            throw new NotFoundException("Фильм или пользователь не найдены");
        }
    }
}
