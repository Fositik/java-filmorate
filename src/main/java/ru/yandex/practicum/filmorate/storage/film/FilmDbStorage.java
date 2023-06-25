package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import ru.yandex.practicum.filmorate.storage.ratingmpa.RatingMpaStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
@Qualifier("FilmDbStorage")
@Slf4j
@RequiredArgsConstructor
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
        // Создаем объект RowMapper для отображения строк результата запроса в объекты Film
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new RatingMPA(rs.getInt("rating_id")));

            try {
                // Получаем объект RatingMPA по его идентификатору из хранилища ratingMpaStorage
                film.setMpa(ratingMpaStorage.getRatingMpaById(film.getMpa().getId()));
            } catch (NotFoundException e) {
                throw new SQLException("MPA в этом фильме не найден");
            }

            try {
                // Получаем список жанров для фильма по его идентификатору из хранилища genreStorage
                List<Genre> genres = genreStorage.getGenresByFilmId(film.getId());
                film.setGenres();
            } catch (Exception e) {
                throw new SQLException("Проблема при получении жанров для фильма", e);
            }

            return film;
        };
    }

    @Override
    public Film addFilm(Film film){
        String sql = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION,RELEASE_DATE, DURATION,  RATING_ID) " +
                " VALUES(? , ? , ? , ? ,  ?)";

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
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET film_name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE film_id = ?;";

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
            updateGenresNameById(film);
            saveGenres(film);
        } catch (NotFoundException e) {
            log.info("Failed to retrieve genres or MPA rating for film: {}", film);
            throw new RuntimeException("Failed to retrieve genres or MPA rating for film.", e);
        }
        log.info("Фильм обновлен: {}", film);

        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        log.info("Получение фильма по ID: {}", id);
        String sql = "SELECT * FROM films WHERE film_id = ? ;";
        try {
            // Получаем фильм по его идентификатору из таблицы FILMS
            Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, id);
            List<Genre> genres = genreStorage.getGenresByFilmId(id);

            // Устанавливаем список жанров для фильма
            if (film != null) {
                film.setGenres(genres);
            }

            System.out.println(genres.toString());
            log.info("Фильм под id: {} получен", id);
            return film;
        } catch (EmptyResultDataAccessException e) {
            log.info("Фильм под id: {} не найден", id);
            throw new NotFoundException("Film не найден.");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films;";
        // Получаем список всех фильмов из таблицы FILMS
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        films.forEach(film -> {
            try {
                // Получаем список жанров для каждого фильма
                List<Genre> genres = genreStorage.getGenresByFilmId(film.getId());

                System.out.println(genres.toString());

                // Устанавливаем список жанров для фильма
                film.setGenres(genres);
            } catch (NotFoundException e) {
                throw new RuntimeException("Не удалось получить жанры для фильма с id "+ film.getId(), e);
            }
        });
        return films;
    }

    @Override
    public List<Long> addLikeToFilm(Long filmId, Long userId) {
        String sql = "INSERT INTO film_user_likes (film_id, user_id) VALUES (?, ?);";
        // Добавляем лайк к фильму
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Добавление лайка к фильму с id: {} польщователем с id: {}", filmId,userId);
        // Возвращаем список идентификаторов пользователей, оставивших лайки для фильма
        return new ArrayList<>(getFilmLikes(filmId));
    }

    @Override
    public List<Long> removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_user_likes WHERE film_id = ? AND user_id = ?;";
        // Удаляем лайк у фильма
        jdbcTemplate.update(sql, filmId, userId);

        // Получаем список идентификаторов пользователей, оставивших лайки для фильма
        getFilmLikes(filmId);
        log.info("У лайка к фильму с id: {} польщователем с id: {}", filmId,userId);
        // Возвращаем список идентификаторов пользователей, оставивших лайки для фильма
        return new ArrayList<>(getFilmLikes(filmId));
    }

    @Override
    public Set<Long> getFilmLikes(Long filmId) {
        String sql = "SELECT user_id FROM film_user_likes WHERE film_id = ?;";

        log.info("Получение лайков для фильма с ID: {}", filmId);
        // Получаем список идентификаторов пользователей, оставивших лайки для фильма
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

        // Выполняется SQL-запрос, чтобы получить список популярных фильмов
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, count);

        // Для каждого фильма в списке выполняются дополнительные операции
        films.forEach(film -> {
            try {
                // Получаем количество лайков для каждого фильма
                getFilmLikes(film.getId());

                // Получаем жанры фильма по его идентификатору из хранилища (например, базы данных)
                List<Genre> genres = genreStorage.getGenresByFilmId(film.getId());

                // Устанавливаем полученные жанры фильма
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


    private void updateMpaRating(Film film) {
        film.setMpa(ratingMpaStorage.getRatingMpaById(film.getMpa().getId()));
    }


    private void updateGenresNameById(Film film) {
        // Проверяем, есть ли у фильма жанры
        if (film.getGenres() == null) {
            return; // Если жанров нет, выходим из метода
        }

        List<Genre> genresWithName = new ArrayList<>(); // Создаем список для хранения жанров с именами
        Set<Integer> doubleId = new HashSet<>(); // Создаем множество для отслеживания дублирующихся идентификаторов жанров

        // Перебираем жанры фильма
        for (Genre genre : film.getGenres()) {
            // Проверяем, не содержит ли множество уже идентификатор текущего жанра
            if (!doubleId.contains(genre.getId())) {
                doubleId.add(genre.getId()); // Добавляем идентификатор жанра в множество
                // Получаем жанр с указанным идентификатором из хранилища (например, базы данных)
                genresWithName.add(genreStorage.getGenreById(genre.getId()));
            }
        }

        film.setGenres(genresWithName); // Обновляем жанры фильма, заменяя их на жанры с именами из хранилища
    }


    private void saveGenres(Film film) {
        long filmId = film.getId();

        // Удаляем все связи между фильмом и жанрами
        String deleteSql = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(deleteSql, filmId);

        // Если у фильма есть жанры, сохраняем связи между фильмом и жанрами
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertSql = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
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
        try {
            // Удаляем связанные записи из таблицы film_genres
            String deleteGenresSql = "DELETE FROM film_genres WHERE film_id = ?";
            jdbcTemplate.update(deleteGenresSql, id);

            // Удаляем связанные записи из таблицы film_user_likes
            String deleteLikesSql = "DELETE FROM film_user_likes WHERE film_id = ?";
            jdbcTemplate.update(deleteLikesSql, id);

            // Затем удаляем сам фильм из таблицы films
            String deleteFilmSql = "DELETE FROM films WHERE film_id = ?";
            int rowsAffected = jdbcTemplate.update(deleteFilmSql, id);
            if (rowsAffected == 0) {
                throw new NotFoundException("Film not found");
            }
            log.info("Удаление фильма с id: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.info("Фильм с id: {} не найлен", id);
            throw new NotFoundException("Film not found");
        }
    }
}



