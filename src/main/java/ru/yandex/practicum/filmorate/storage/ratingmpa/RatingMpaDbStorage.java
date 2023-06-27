package ru.yandex.practicum.filmorate.storage.ratingmpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class RatingMpaDbStorage implements RatingMpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<RatingMPA> ratingMPARowMapper;

    public RatingMpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingMPARowMapper = (rs, rowNum) -> new RatingMPA(rs
                .getInt("rating_id"), rs.getString("rating_name"));
    }

    public Optional<RatingMPA> getRatingMpaById(int ratingId) {
        String sqlQuery = RatingMpaSQLQueries.SELECT_RATING_BY_ID;
        try {
            RatingMPA result = jdbcTemplate.queryForObject(sqlQuery, ratingMPARowMapper, ratingId);
            return Optional.of(result);
        } catch (EmptyResultDataAccessException e) {
            log.error("No RatingMPA found for id: {}", ratingId);
            return Optional.empty();
        }
    }

    public List<RatingMPA> getAllRatings() {
        String sqlQuery = RatingMpaSQLQueries.SELECT_ALL_RATINGS;
        log.info("Получение списка всех RatingMPA");
        return jdbcTemplate.query(sqlQuery, ratingMPARowMapper);
    }
}
