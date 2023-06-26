package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class FilmController {

    private final FilmService filmService;


    @PostMapping  //@PostMapping указывает, что этот метод обрабатывает HTTP POST-запросы
    public Film addFilm(@Valid @RequestBody Film film) {
        Film createdFilm = filmService.addFilm(film);
        log.info("Добавление фильма: {}", film);

        return createdFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        log.info("Обновление фильма с id={}: {}", updatedFilm.getId(), updatedFilm);
        filmService.updateFilm(updatedFilm);

        return updatedFilm;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получение списка всех фильмов");

        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@Positive @PathVariable(name = "id") Long id) {
        log.info("Получение фильма с id={}", id);

        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToFilm(@Positive @PathVariable(name = "id") long id,
                              @Positive @PathVariable(name = "userId") long userId) {
        log.info("Добавление лайка к фильму под id: {} от пользователя с id: {}", id, userId);

        filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable(name = "id") long id,
                           @PathVariable(name = "userId") long userId) {
        log.info("Удаление лайка с фильма под id: {} от пользователя с id: {}", id, userId);

        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") @Positive(message = "Количество фильмов " +
            "должно быть положительным") Long count) {
        log.info("Получение списка из топ {} фильмов", count);

        return filmService.getTopFilms(count);
    }

}
