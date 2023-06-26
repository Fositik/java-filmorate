package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> genreRowMapper;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreRowMapper = (rs, rowNum) -> new Genre(rs
                .getInt("genre_id"), rs.getString("genre_name"));
    }

    @Override
    public Optional<Genre> getGenreById(int genreId) {
        String sqlQuery = GenreSQLQueries.SELECT_GENRE_BY_ID;
        try {
            //  log.info("Получение RatingMPA под id: {}", genreId);
            Genre result = jdbcTemplate.queryForObject(sqlQuery, genreRowMapper, genreId);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            log.info("Жанр под id: {} не найден", genreId);
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = GenreSQLQueries.SELECT_ALL_GENRES;
        log.info("Получение списка всех жанров");
        return jdbcTemplate.query(sqlQuery, genreRowMapper);
    }

    @Override
    public LinkedHashSet<Genre> getGenresByFilmId(Long filmId) {
        String sql = GenreSQLQueries.SELECT_GENRE_BY_FILM_ID;
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> {
            int id = rs.getInt("genre_id");
            String name = rs.getString("genre_name");
            return new Genre(id, name);
        }, filmId);
        log.info("Retrieved Genres: {}", genres);
        return new LinkedHashSet<>(genres);
    }
}
