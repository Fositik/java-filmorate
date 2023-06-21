package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.util.validators.FilmValidator;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    private FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping  //@PostMapping указывает, что этот метод обрабатывает HTTP POST-запросы
    public Film addFilm(@Valid @RequestBody Film film) {
        FilmValidator.validateFilm(film);
       Film createdFilm =  filmService.addFilm(film);
        log.info("Добавление фильма: {}", film);

        return createdFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        FilmValidator.validateFilm(updatedFilm);
        log.info("Обновление фильма с id={}: {}", updatedFilm.getId(), updatedFilm);
        filmService.updateFilm(updatedFilm);

        return updatedFilm;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получение списка всех фильмов");
        List<Film> films = filmService.getAllFilms();

        return films;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(name = "id") Long id) throws NotFoundException {
        log.info("Получение фильма с id={}", id);
        Film film = filmService.getFilmById(id);

        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable(name = "id") long id, @PathVariable(name = "userId") long userId) {
        log.info("Добавление лайка к фильму под id: {} от пользователя с id: {}", id, userId);

        filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable(name = "id") long id, @PathVariable(name = "userId") long userId) {
        log.info("Удаление лайка с фильма под id: {} от пользователя с id: {}", id, userId);

        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10", required = false) Long count) {
        log.info("Получение списка из топ {} фильмов", count);

        List<Film> filmsRating = filmService.getTopFilms(count);
        return filmsRating;
    }
}
