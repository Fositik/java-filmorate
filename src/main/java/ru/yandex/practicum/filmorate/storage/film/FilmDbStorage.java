
package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.CreateUserException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import ru.yandex.practicum.filmorate.storage.ratingmpa.RatingMpaStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("FilmDbStorage")
@Slf4j
//@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RatingMpaStorage ratingMpaStorage;
    private final GenreStorage genreStorage;
    private final RowMapper<Film> filmRowMapper;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, RatingMpaStorage ratingMpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingMpaStorage = ratingMpaStorage;
        this.genreStorage = genreStorage;
        this.filmRowMapper = createRowMapper();
    }


    private RowMapper<Film> createRowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new RatingMPA(rs.getInt("rating_id")));
            try {
                film.setMpa(ratingMpaStorage.getRatingMpaById(film.getMpa().getId()));
            } catch (NotFoundException e) {
                throw new SQLException("MPA в этом фильме не найден");
            }
            try {
                List<Genre> genres = genreStorage.getGenresByFilmId(film.getId());
                film.setGenres(genres);
            } catch (Exception e) {
                throw new SQLException("Проблема при получении жанров для фильма", e);
            }
            return film;
        };
    }

    @Override
    public Film addFilm(Film film) throws NotFoundException {
        String sql = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION,RELEASE_DATE, DURATION,  RATING_ID) " +
                " VALUES(? , ? , ? , ? ,  ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement prSt = connection.prepareStatement(
                            sql
                            , new String[]{"film_id"});
                    prSt.setString(1, film.getName());
                    prSt.setString(2, film.getDescription());
                    prSt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));

                    prSt.setLong(4, film.getDuration());

                    //   prSt.setLong(5, filmDto.getRate() == null ? 0 : filmDto.getRate());
                    prSt.setLong(5, film.getMpa().getId());
                    return prSt;
                }
                , keyHolder);

        updateMpaRating(film);
        updateGenresNameById(film);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        saveGenres(film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET film_name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE film_id = ?;";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        try {
            updateMpaRating(film);
            updateGenresNameById(film);
            saveGenres(film);
        } catch (NotFoundException e) {
            throw new RuntimeException("Failed to retrieve genres or MPA rating for film.", e);
        }

        return film;
    }


    @Override
    public Film getFilmById(Long id) throws NotFoundException {
        log.info("Получение фильма по ID: {}", id);
        String sql = "SELECT * FROM films WHERE film_id = ? ;";
        try {
            Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, id);
            List<Genre> genres = genreStorage.getGenresByFilmId(id);
            film.setGenres(genres);
            System.out.println(genres.toString());
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Film не найден.");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films;";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        films.forEach(film -> {
            try {
                List<Genre> genres = genreStorage.getGenresByFilmId(film.getId());

                System.out.println(genres.toString());

                film.setGenres(genres);
            } catch (NotFoundException e) {
                throw new RuntimeException("Не удалось получить жанры для фильма с id " + film.getId(), e);
            }
        });
        return films;
    }


    @Override
    public List<Long> addLikeToFilm(Long filmId, Long userId) {
        String sql = "INSERT INTO film_user_likes (film_id, user_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, filmId, userId);

        return new ArrayList<>(getFilmLikes(filmId));
    }

    @Override
    public List<Long> removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_user_likes WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(sql, filmId, userId);

        getFilmLikes(filmId);

        return new ArrayList<>(getFilmLikes(filmId));
    }

    @Override
    public Set<Long> getFilmLikes(Long filmId) {
        String sql = "SELECT user_id FROM film_user_likes WHERE film_id = ?;";

        log.info("Получение лайков для фильма с ID: {}", filmId);
        List<Long> likes = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
        Set<Long> likesSet = new HashSet<>(likes);

        log.info("Получено {} лайков для фильма с ID: {}", likesSet.size(), filmId);
        return likesSet;
    }


    @Override
    public List<Film> getTopFilms(Long count) {
        String sql = "SELECT f.*, COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN film_user_likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC NULLS LAST, f.film_id ASC " +
                "LIMIT ?;";

        log.info("Получение списка популярных фильмов (количество: {})", count);
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, count);
        films.forEach(film -> {
            try {
                getFilmLikes(film.getId());
                List<Genre> genres = genreStorage.getGenresByFilmId(film.getId());
                film.setGenres(genres);
                log.error("Фильм с id {}", film.getId());
            } catch (NotFoundException e) {
                log.error("Не удалось получить жанры для фильма с id {}", film.getId(), e);
                throw new RuntimeException("Не удалось получить жанры для фильма с id " + film.getId(), e);
            }
        });

        log.info("Получено {} популярных фильмов", films.size());
        return films;
    }

    private void updateMpaRating(Film film) throws NotFoundException {
        film.setMpa(ratingMpaStorage.getRatingMpaById(film.getMpa().getId()));
    }


    private void updateGenresNameById(Film film) throws NotFoundException {
        if (film.getGenres() == null) {
            return;
        }
        List<Genre> genresWithName = new ArrayList<>();
        Set<Integer> doubleId = new HashSet<>();
        for (Genre genre : film.getGenres()) {
            if (!doubleId.contains(genre.getId())) {
                doubleId.add(genre.getId());
                genresWithName.add(genreStorage.getGenreById(genre.getId()));
            }
        }
        film.setGenres(genresWithName);
    }

    private void saveGenres(Film film) {
        long filmId = film.getId();
        String deleteSql = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(deleteSql, filmId);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertSql = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
            List<Object[]> batchArgs = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                Object[] params = {filmId, genre.getId()};
                batchArgs.add(params);
            }
            jdbcTemplate.batchUpdate(insertSql, batchArgs);
        }
    }

}



