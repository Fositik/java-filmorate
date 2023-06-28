package ru.yandex.practicum.filmorate.storage.filmlikes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmLikeDbStorage implements FilmLikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserService userService;


    @Override
    public void addLikeToFilm(Long filmId, Long userId) {
        String sql = FilmLikeSQLQueries.INSERT_FILM_USER_LIKES;
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Добавление лайка к фильму с id: {} пользователем с id: {}", filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        userService.validateUserId(userId);
        String sql = FilmLikeSQLQueries.DELETE_FILM_USER_LIKES;
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Удаление лайка у фильма с id: {} пользователем с id: {}", filmId, userId);
    }

    @Override
    public Set<Long> getFilmLikes(Long filmId) {
        String sql = FilmLikeSQLQueries.SELECT_FILM_LIKES;

        log.info("Получение лайков для фильма с ID: {}", filmId);
        List<Long> likes = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
        Set<Long> likesSet = new HashSet<>(likes);

        log.info("Получено {} лайков для фильма с ID: {}", likesSet.size(), filmId);
        return likesSet;
    }

}
