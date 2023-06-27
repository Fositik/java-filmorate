package ru.yandex.practicum.filmorate.storage.filmlikes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FilmLikeDbStorage implements FilmLikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(long filmId, long userId, int rate) {
        String sql = FilmLikeSQLQueries.INSERT_LIKE;
        try {
            jdbcTemplate.update(sql, filmId, userId);
        } catch (DataAccessException e) {
            log.error("Ошибка при добавлении лайка к фильму с id: {} пользователем с id: {}", filmId, userId, e);
            throw new NotFoundException("Фильм или пользователь не найдены");
        }
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sql = FilmLikeSQLQueries.REMOVE_LIKE;
        try {
            int deletedRows = jdbcTemplate.update(sql, filmId, userId);
            if (deletedRows == 0) {
                throw new NotFoundException(String.format(
                        "Лайк от пользователя %d к фильму %d не найден", userId, filmId)
                );
            }
        } catch (DataAccessException e) {
            log.error("Ошибка при удалении лайка с фильма под id {} пользователем с id {}", filmId, userId, e);
            throw new NotFoundException("Фильм или пользователь не найдены");
        }
    }
    @Override
    public boolean isLikedByUser(long filmId, long userId) {
        String sql = FilmLikeSQLQueries.IS_LIKED_BY_USER;
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
            return count > 0;
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка при проверке лайка к фильму под id {} пользователем с id {}", filmId, userId, e);
            throw new NotFoundException("Фильм или пользователь не найдены");
        }
    }
}
