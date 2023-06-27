package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> genreRowMapper = genreRowMapper();

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            return genre;
        };
    }

    @Override
    public Optional<Genre> getGenreById(int genreId) {
        String sqlQuery = GenreSQLQueries.SELECT_GENRE_BY_ID;
        try {
            Genre result = jdbcTemplate.queryForObject(sqlQuery, genreRowMapper, genreId);
            return Optional.of(result);
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
        List<Genre> genres = jdbcTemplate.query(sql, genreRowMapper, filmId);
        log.info("Retrieved Genres: {}", genres);
        return new LinkedHashSet<>(genres);
    }

    @Override
    public Map<Long, LinkedHashSet<Genre>> getGenresByFilmIds(List<Long> filmIds) {
        // Если список filmIds пуст или равен null, возвращаем пустую мапу
        // Это сделано для предотвращения выполнения ненужного запроса к базе данных
        if (filmIds == null || filmIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // Создаем мапу для хранения результата
        // Ключом будет идентификатор фильма, значением - набор жанров этого фильма
        Map<Long, LinkedHashSet<Genre>> genresByFilmId = new HashMap<>();

        // SQL-запрос для получения жанров по списку идентификаторов фильмов
        // IN (:filmIds) позволяет указать динамический список идентификаторов
        String sql = GenreSQLQueries.SELECT_GENRES_IN_FILM;

        // Создаем MapSqlParameterSource и добавляем в него параметр "filmIds"
        // Он будет использован для замены маркера :filmIds в SQL-запросе
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("filmIds", filmIds);

        // Создаем NamedParameterJdbcTemplate, который позволяет работать с именованными параметрами
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());

        // Выполняем запрос, который будет обрабатывать каждую строчку результата
        namedParameterJdbcTemplate.query(sql, parameters, (rs, rowNum) -> {
            // Получаем идентификатор фильма и жанр из текущей строки
            Long filmId = rs.getLong("film_id");
            Genre genre = genreRowMapper().mapRow(rs, rowNum);

            // Если мапа еще не содержит такого ключа (film_id),
            // то добавляем его с пустым списком жанров
            if (!genresByFilmId.containsKey(filmId)) {
                genresByFilmId.put(filmId, new LinkedHashSet<>());
            }

            // Добавляем текущий жанр в список жанров фильма
            genresByFilmId.get(filmId).add(genre);

            return null;  // Ничего не возвращаем, потому что все данные сохраняются в мапе genresByFilmId
        });

        // Возвращаем полученную Мапу с жанрами для каждого фильма
        return genresByFilmId;
    }

}
