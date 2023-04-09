package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmStorage filmStorage;

    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    private static final int LIMIT_LENGTH_OF_DESCRIPTION = 200;                     //максимальная длина описания
    //дата релиза
    private static final LocalDate LIMIT_DATE = LocalDate.from(
            LocalDateTime.of(1895, 12, 28, 0, 0)
    );
    private final List<Film> films = new ArrayList<>();



    /**
     * Добавляет новый фильм
     *
     * @param film объект Film {@link Film}, который содержит данные фильма
     * @return объект Film c добавленным фильмом
     */
    @PostMapping  //@PostMapping указывает, что этот метод обрабатывает HTTP POST-запросы
    public Film addFilm(@Valid @RequestBody Film film) {
        validate(film);
        log.info("Добавление фильма: {}", film);
        return filmStorage.addFilm(film);
    }

    /**
     * Обновляет данные фильма
     *
     * @param updatedFilm объект Film {@link Film}, который содержит данные обновленного фильма
     * @return Film, содержащий обновленный фильм
     * @throws ValidationException если форма обновления фильма заполнена неправильно или фильм с указанным id не найден
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        validate(updatedFilm);
        log.info("Обновление фильма с id={}: {}", updatedFilm.getId(), updatedFilm);
        return filmStorage.updateFilm(updatedFilm);
    }

    /**
     * Возвращает список всех фильмов
     *
     * @return список List фильмов
     */
    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получение списка всех фильмов");
        return filmStorage.getAllFilms();
    }

    /**
     * Метод проверяет соответствие данных фильма следующим условиям:
     * - Название фильма не может быть пустым.
     * - Описание фильма не может быть пустым и не может превышать 200 символов.
     * - Дата релиза фильма не может быть раньше 28 декабря 1895 года.
     * - Продолжительность фильма должна быть положительной.
     *
     * @param film объект Film {@link Film}, который содержит данные проверяемого на соответствие условиям фильма
     */
    protected void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Отсутствует название фильма (null or blank).");
            throw new ValidationException("Название не может быть пустым.");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            log.debug("Отсутствует описание фильма (null or blank).");
            throw new ValidationException("Описание фильма не может быть пустым.");
        } else if (film.getDescription().length() > LIMIT_LENGTH_OF_DESCRIPTION) {
            log.debug("Длина описания превышает лимит в 200 символов.");
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LIMIT_DATE)) {
            log.debug("Переданная дата релиза — раньше 28 декабря 1895 года.");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            log.debug("Продолжительность фильма <=0.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }
}
