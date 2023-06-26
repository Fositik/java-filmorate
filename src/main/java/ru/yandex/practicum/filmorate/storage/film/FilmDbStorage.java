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
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RatingMpaDbStorage ratingMpaStorage;
    private final GenreDbStorage genreStorage;
    private final UserDbStorage userStorage;
    private final RowMapper<Film> filmRowMapper = createRowMapper();

    private RowMapper<Film> createRowMapper() {
        // Создаем объект RowMapper для отображения строк результата запроса в объекты Film
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getObject("release_date", LocalDate.class));
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new RatingMPA(rs.getInt("rating_id")));

            ratingMpaStorage.getRatingMpaById(film.getMpa().getId()).ifPresent(film::setMpa);

            LinkedHashSet<Genre> genres = getGenresByFilmId(film.getId());
            film.setGenres(genres);

            return film;
        };
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Film addFilm(Film film) {
        String sql = FilmSQLQueries.INSERT_FILM;

        // Исполняем SQL-запрос на вставку данных в таблицу FILMS с использованием PreparedStatement
        // и получаем сгенерированный идентификатор фильма
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

        // Обновляем MPA-рейтинг и жанры для фильма
        updateMpaRating(film);
        updateGenresNameById(film);

        // Устанавливаем идентификатор фильма, полученный из keyHolder
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        // Сохраняем жанры фильма
        saveGenres(film);

        log.info("фильм создан: {}", film);
        return film;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Film updateFilm(Film film) {
        filmExists(film.getId());

        String sql = FilmSQLQueries.UPDATE_FILM;

        // Исполняем SQL-запрос на обновление данных фильма в таблице FILMS
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        try {
            // Обновляем MPA-рейтинг и жанры для фильма
            updateMpaRating(film);
            saveGenres(film);
            updateGenresNameById(film);
        } catch (NotFoundException e) {
            log.error("Не удалось получить жанры и рейтинг для фильма с id {}", film.getId(), e);
            throw new FilmNotFoundException("Не удалось получить жанры и рейтинг для фильма с id " + film.getId(), e);
        }
        log.info("Фильм обновлен: {}", film);

        return film;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        log.info("Получение фильма по ID: {}", id);
        String sql = FilmSQLQueries.SELECT_FILM_BY_ID;
        try {
            Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, id);
            if (film != null) {
                film.setGenres(getGenresByFilmId(id));
            }
            log.error("Фильм под id: {} получен", id);
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            log.error("Фильм под id: {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = FilmSQLQueries.SELECT_ALL_FILMS;
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        films.forEach(this::setGenresForFilm);
        return films;
    }

    @Override
    public void addLikeToFilm(Long filmId, Long userId) {
        filmExists(filmId);
        String sql = FilmSQLQueries.INSERT_FILM_USER_LIKES;
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Добавление лайка к фильму с id: {} пользователем с id: {}", filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        filmExists(filmId);
        userStorage.userExists(userId);
        String sql = FilmSQLQueries.DELETE_FILM_USER_LIKES;
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Удаление лайка у фильма с id: {} пользователем с id: {}", filmId, userId);
    }

    @Override
    public Set<Long> getFilmLikes(Long filmId) {
        filmExists(filmId);
        String sql = FilmSQLQueries.SELECT_FILM_LIKES;

        log.info("Получение лайков для фильма с ID: {}", filmId);
        // Получаем список идентификаторов пользователей, оставивших лайки для фильма
        List<Long> likes = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
        Set<Long> likesSet = new HashSet<>(likes);

        log.info("Получено {} лайков для фильма с ID: {}", likesSet.size(), filmId);
        return likesSet;
    }

    @Override
    public List<Film> getTopFilms(Long count) {
        String sql = FilmSQLQueries.SELECT_TOP_FILMS;

        log.info("Получение списка популярных фильмов (количество: {})", count);
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, count);
        films.forEach(film -> {
            getFilmLikes(film.getId());
            setGenresForFilm(film);
        });

        log.info("Получено {} популярных фильмов", films.size());
        return films;
    }

    private void updateMpaRating(Film film) {
        ratingMpaStorage.getRatingMpaById(film.getMpa().getId()).ifPresent(film::setMpa);
    }

    private void updateGenresNameById(Film film) {
        if (film.getGenres() == null) {
            return;
        }

        LinkedHashSet<Genre> genresWithName = new LinkedHashSet<>();
        Set<Integer> doubleId = new HashSet<>();

        for (Genre genre : film.getGenres()) {
            if (!doubleId.contains(genre.getId())) {
                doubleId.add(genre.getId());
                genreStorage.getGenreById(genre.getId()).ifPresent(genresWithName::add);
            }
        }

        film.setGenres(genresWithName);
    }

    private void saveGenres(Film film) {
        long filmId = film.getId();

        // Удаляем все связи между фильмом и жанрами
        String deleteSql = FilmSQLQueries.DELETE_FILM_GENRES;
        jdbcTemplate.update(deleteSql, filmId);

        // Если у фильма есть жанры, сохраняем связи между фильмом и жанрами
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertSql = FilmSQLQueries.INSERT_FILM_GENRES;
            List<Object[]> batchArgs = new ArrayList<>();

            // Для каждого жанра в списке жанров фильма
            for (Genre genre : film.getGenres()) {
                Object[] params = {filmId, genre.getId()};
                batchArgs.add(params); // Добавляем параметры для выполнения пакетного обновления
            }

            // Выполняем пакетное обновление, вставляя связи между фильмом и жанрами
            jdbcTemplate.batchUpdate(insertSql, batchArgs);
        }
    }

    @Override
    public void removeFilm(Long id) {
        filmExists(id);

        try {
            // Удаляем связанные записи из таблицы film_genres
            String deleteGenresSql = FilmSQLQueries.DELETE_FILM_GENRES;
            jdbcTemplate.update(deleteGenresSql, id);

            // Удаляем связанные записи из таблицы film_user_likes
            String deleteLikesSql = FilmSQLQueries.DELETE_FILM_USER_LIKES;
            jdbcTemplate.update(deleteLikesSql, id);

            // Затем удаляем сам фильм из таблицы films
            String deleteFilmSql = FilmSQLQueries.DELETE_FILM;
            jdbcTemplate.update(deleteFilmSql, id);
            log.info("Удаление фильма с id: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Произошла ошибка при удалении фильма с id: {}", id, e);
            throw new RuntimeException("Ошибка при удалении фильма с id: " + id, e);
        }
    }

    private LinkedHashSet<Genre> getGenresByFilmId(Long filmId) {
        LinkedHashSet<Genre> genres = genreStorage.getGenresByFilmId(filmId);
        log.info("Получение списка жанров для фильма id: {}", filmId);
        return genres;
    }

    private void setGenresForFilm(Film film) {
        try {
            LinkedHashSet<Genre> genres = getGenresByFilmId(film.getId());
            film.setGenres(genres);
        } catch (NotFoundException e) {
            throw new FilmNotFoundException("Не удалось получить жанры для фильма с id " + film.getId(), e);
        }
    }

    private void filmExists(Long id) {
        String checkSql = FilmSQLQueries.FILM_EXISTS;
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);
        if (count == null || count == 0) {
            log.error("Фильм с id {} не найден", id);
            throw new NotFoundException("Film with id " + id + " not found");
        }
    }
}



