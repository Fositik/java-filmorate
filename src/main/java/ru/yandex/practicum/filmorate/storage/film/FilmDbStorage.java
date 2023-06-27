package ru.yandex.practicum.filmorate.storage.film;

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
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.ratingmpa.RatingMpaDbStorage;


import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RatingMpaDbStorage ratingMpaStorage;
    private final GenreDbStorage genreStorage;

    private final RowMapper<Film> filmRowMapper = createRowMapper();

    private RowMapper<Film> createRowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getObject("release_date", LocalDate.class));
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new RatingMPA(rs.getInt("rating_id")));

            ratingMpaStorage.getRatingMpaById(film.getMpa().getId()).ifPresent(film::setMpa);

            return film;
        };
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Film addFilm(Film film) {
        String sql = FilmSQLQueries.INSERT_FILM;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement prSt = connection.prepareStatement(
                            sql, new String[]{"film_id"});
                    prSt.setString(1, film.getName());
                    prSt.setString(2, film.getDescription());
                    prSt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
                    prSt.setLong(4, film.getDuration());
                    prSt.setLong(5, film.getMpa().getId());
                    return prSt;
                }, keyHolder);


        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        saveGenres(film);

        log.info("фильм создан: {}", film);
        return film;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    //Update film
    public Film updateFilm(Film film) {
        String sql = FilmSQLQueries.UPDATE_FILM;

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        try {
            saveGenres(film);
        } catch (NotFoundException e) {
            log.error("Не удалось получить жанры и рейтинг для фильма с id {}", film.getId(), e);
            throw new FilmNotFoundException("Не удалось получить жанры и рейтинг для фильма с id " + film.getId(), e);
        }
        log.info("Фильм обновлен: {}", film);

        return film;
    }

    @Override
    public void removeFilm(Long id) {
        try {
            String deleteFilmSql = FilmSQLQueries.DELETE_FILM;
            jdbcTemplate.update(deleteFilmSql, id);
            log.info("Удаление фильма с id: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Произошла ошибка при удалении фильма с id: {}", id, e);
            throw new RuntimeException("Ошибка при удалении фильма с id: " + id, e);
        }
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        log.info("Получение фильма по ID: {}", id);
        String sql = FilmSQLQueries.SELECT_FILM_BY_ID;
        try {
            Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, id);
            LinkedHashSet<Genre> genres = genreStorage.getGenresByFilmId(film.getId());
            film.setGenres(genres); // Заполняем поле 'genres' фильма

            log.error("Фильм под id: {} получен", id);
            return Optional.of(film);
        } catch (EmptyResultDataAccessException e) {
            log.error("Фильм под id: {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getAllFilms() {
        //Извлекаем все фильмы. Только основную информацию (без жанров)
        String sql = FilmSQLQueries.SELECT_ALL_FILMS;
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        //Далее извлекаем все id-ки и сохраняем их в список
        addGenresToFilms(films);
        log.info("Получение списка всех фильмов");
        return films;
    }


    @Override
    public List<Film> getTopFilms(Long count) {
        String sql = FilmSQLQueries.SELECT_TOP_FILMS;

        log.info("Получение списка популярных фильмов (количество: {})", count);
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, count);

        addGenresToFilms(films);

        log.info("Получено {} популярных фильмов", films.size());
        return films;
    }

    @Override
    public boolean filmExists(long filmId) {
        String checkSql = FilmSQLQueries.FILM_EXISTS;
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, filmId);
        return count != null && count > 0;
    }

    private void addGenresToFilms(List<Film> films) {
        List<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());
        Map<Long, LinkedHashSet<Genre>> genresByFilmId = genreStorage.getGenresByFilmIds(filmIds);
        films.forEach(film -> {
            LinkedHashSet<Genre> genres = genresByFilmId.getOrDefault(film.getId(), new LinkedHashSet<>());
            film.setGenres(genres);
        });
    }

    private void saveGenres(Film film) {
        long filmId = film.getId();

        String deleteSql = FilmSQLQueries.DELETE_FILM_GENRES;
        jdbcTemplate.update(deleteSql, filmId);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertSql = FilmSQLQueries.INSERT_FILM_GENRES;
            List<Object[]> batchArgs = new ArrayList<>();

            for (Genre genre : film.getGenres()) {
                Object[] params = {filmId, genre.getId()};
                batchArgs.add(params);
            }

            jdbcTemplate.batchUpdate(insertSql, batchArgs);
        }
    }
}



