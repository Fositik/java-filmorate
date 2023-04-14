package ru.yandex.practicum.filmorate.util.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public class FilmValidator {
    private static final int LIMIT_LENGTH_OF_DESCRIPTION = 200;                     //максимальная длина описания
    //дата релиза
    private static final LocalDate LIMIT_DATE = LocalDate.of(1895, 12, 28);

    public static void validateFilm(Film film) throws ValidationException {
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

    public static void validateCreate(List<Long> filmIds, Film createdFilm) throws ValidationException {
        if (createdFilm.getId() != null) {
            if (filmIds.contains(createdFilm.getId())) {
                log.info("Фильм уже существует: {}", createdFilm);
                throw new ValidationException("Такой фильм уже существует");
            }
        }
    }

    public static void validateUpdate(List<Long> filmIds, Film updatedFilm) throws ValidationException, NotFoundException {
        if (updatedFilm.getId() == null) {
            log.info("Фильм еще не был создан: {}", updatedFilm);
            throw new ValidationException("Фильм еще не  был создан");
        }
        if (!filmIds.contains(updatedFilm.getId())) {
            log.info("Фильм еще не был создан: {}", updatedFilm);
            throw new NotFoundException("Фильм еще не  был создан");
        }
    }

    public static void validateExist(List<Long> filmIds, Long id) throws NotFoundException {
        if (!filmIds.contains(id)) {
            log.info(String.format("Фильм с id %s не найден", id));
            throw new NotFoundException(String.format("Фильм с id %s не найден", id));
        }
    }

    public static void validateLike(List<Long> likedBy, Long id) throws ValidationException {
        if (likedBy.contains(id)) {
            log.error("Пользователь уже поставил лайк данному фильму: {}", likedBy);
            throw new ValidationException("Пользователь уже поставил лайк данному фильму");
        }
    }
}
