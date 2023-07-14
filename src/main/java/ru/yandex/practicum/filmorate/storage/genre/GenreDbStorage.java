package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Genre> genreRowMapper = createRowMapper();

    private RowMapper<Genre> createRowMapper() {
        return (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            return genre;
        };
    }

    @Override
    public Optional<Genre> getGenreById(int genreId) {
        try {
            Genre result = jdbcTemplate.queryForObject(GenreSQLQueries.SELECT_GENRE_BY_ID, genreRowMapper, genreId);
            log.info("Жанр под id: {}", genreId);
            return Optional.of(result);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Жанр под id: {} не найден", genreId);
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        log.info("Получение списка всех жанров");
        return jdbcTemplate.query(GenreSQLQueries.SELECT_ALL_GENRES, genreRowMapper);
    }

    @Override
    public LinkedHashSet<Genre> getGenresByFilmId(Long filmId) {
        List<Genre> genres = jdbcTemplate.query(GenreSQLQueries.SELECT_GENRE_BY_FILM_ID, genreRowMapper, filmId);
        log.info("Жанры для фильма под id: {}: {}", filmId, genres);
        return new LinkedHashSet<>(genres);
    }


    public void load(List<Film> films) {
        log.info("Переданный в метод список films: {}", films);
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        List<Long> filmIds = new ArrayList<>(filmById.keySet());

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));

        jdbcTemplate.query(String.format(GenreSQLQueries.SELECT_LOAD, inSql), (
                rs, rowNum) -> {
            final Film film = filmById.get(rs.getLong("FILM_ID"));
            if (film != null) {
                film.getGenres().add(genreRowMapper.mapRow(rs, rowNum));
            } else {
                log.warn("Фильм с id {} не найден в filmById", rs.getLong("FILM_ID"));
            }
            return null;
        }, filmIds.toArray());
    }

    public void saveGenres(Film film) {
        long filmId = film.getId();

        jdbcTemplate.update(GenreSQLQueries.DELETE_FILM_GENRES, filmId);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Object[]> batchArgs = new ArrayList<>();

            for (Genre genre : film.getGenres()) {
                Object[] params = {filmId, genre.getId()};
                batchArgs.add(params);
            }

            jdbcTemplate.batchUpdate(GenreSQLQueries.INSERT_FILM_GENRES, batchArgs);
        }
        log.info("Жанры фильма {}", film.getGenres());
    }

}
