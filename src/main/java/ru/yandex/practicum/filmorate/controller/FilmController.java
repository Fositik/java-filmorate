package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static final int LIMIT_LENGTH_OF_DESCRIPTION = 200;                     //максимальная длина описания
    //дата релиза
    private static final LocalDate LIMIT_DATE = LocalDate.from(LocalDateTime.of(1895, 12, 28, 0, 0));
    private final List<Film> films = new ArrayList<>();

    /**
     * Добавляет новый фильм
     *
     * @param film объект Film {@link Film}, который содержит данные фильма
     * @return возвращает объект ResponseEntity<Film> с кодом состояния HTTP 201 CREATED и добавленным фильмом в теле ответа.
     */
    @PostMapping  //@PostMapping указывает, что этот метод обрабатывает HTTP POST-запросы
    public Film addFilm(@Valid @RequestBody Film film) {
        validate(film);
        log.info("Добавление фильма: {}", film);
        film.setId(films.size() + 1);
        films.add(film);
        return film;
    }

    /**
     * Обновляет данные фильма
     *
     * @param updatedFilm объект Film {@link Film}, который содержит данные обновленного фильма
     * @return объект ResponseEntity, содержащий обновленный фильм и статус ответа HTTP 200 OK
     * @throws ValidationException если форма обновления фильма заполнена неправильно или фильм с указанным id не найден
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        validate(updatedFilm);
        log.info("Обновление фильма с id={}: {}", updatedFilm.getId(), updatedFilm);
        //Ищем фильм с указанным id в списке фильмов, используя метод filter() и метод findFirst()
        //Если фильм не найден, метод выбрасываем исключение ValidationException с сообщением об ошибке
        Film filmToUpdate = films
                .stream()
                .filter(f -> f.getId() == updatedFilm.getId())
                .findFirst()
                .orElseThrow(() -> new ValidationException("Фильм с id=" + updatedFilm.getId() + " не найден"));
        filmToUpdate.setName(updatedFilm.getName());
        filmToUpdate.setDescription(updatedFilm.getDescription());
        filmToUpdate.setReleaseDate(updatedFilm.getReleaseDate());
        filmToUpdate.setDuration(updatedFilm.getDuration());
        return filmToUpdate;
    }

    /**
     * Возвращает список всех фильмов
     *
     * @return объект ResponseEntity, содержащий список List фильмов
     */
    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получение списка всех фильмов");
        return films;
    }

    /**
     * Метод проверяет соответствие данных фильма следующим условиям:
     * - Название фильма не может быть пустым.
     * - Описание фильма не может быть пустым и не может превышать 200 символов.
     * - Дата релиза фильма не может быть раньше 28 декабря 1895 года.
     * - Продолжительность фильма должна быть положительной.
     *
     * @param film    объект Film {@link Film}, который содержит данные проверяемого на соответствие условиям фильма
     */
    protected void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Название не может быть пустым.");
            throw new ValidationException("Название не может быть пустым.");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            log.debug("Описание фильма не может быть пустым.");
            throw new ValidationException("Описание фильма не может быть пустым.");
        } else if (film.getDescription().length() > LIMIT_LENGTH_OF_DESCRIPTION) {
            log.debug("Максимальная длина описания — 200 символов.");
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LIMIT_DATE)) {
            log.debug("Дата релиза — не раньше 28 декабря 1895 года.");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            log.debug("Продолжительность фильма должна быть положительной.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }
}
