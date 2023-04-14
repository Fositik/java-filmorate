package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.util.validators.FilmValidator;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    private FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping  //@PostMapping указывает, что этот метод обрабатывает HTTP POST-запросы
    public Film addFilm(@Valid @RequestBody Film film) {
        FilmValidator.validateFilm(film);
        filmService.addFilm(film);
        log.info("Добавление фильма: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        FilmValidator.validateFilm(updatedFilm);
        filmService.updateFilm(updatedFilm);
        log.info("Обновление фильма с id={}: {}", updatedFilm.getId(), updatedFilm);
        return updatedFilm;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        List<Film> films = filmService.getAllFilms();
        log.info("Получение списка всех фильмов");
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@Valid @RequestBody Long id) {
        Film film = filmService.getFilmById(id);
        log.info("Получение фильма с id={}", id);
        return film;
    }

    @PostMapping("/{id}/like/{userId}")
    public List<Long> addLikeToFilm(@PathVariable(name = "id") long id, @PathVariable(name = "userId") long userId) {
        List<Long> filmLikes = filmService.addLikeToFilm(id, userId);
        log.info("Добавление лайка к фильму под id: {} от пользователя с id: {}", id, userId);
        return filmLikes;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public List<Long> removeLike(@PathVariable(name = "id") long id, @PathVariable(name = "userId") long userId) {
        List<Long> filmLikes = filmService.removeLike(id, userId);
        log.info("Удаление лайка с фильма под id: {} от пользователя с id: {}", id, userId);
        return filmLikes;
    }

    @GetMapping("/popular?count={count}")
    public List<Film> getTopFilmsByLikes(@RequestBody Integer count) {
        List<Film> filmsRating = filmService.getTopFilmsByLikes(count);
        log.info("Получение списка из топ {} фильмов", count);
        return filmsRating;
    }
}
