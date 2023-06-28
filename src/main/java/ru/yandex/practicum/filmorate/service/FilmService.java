package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmService {
    private final FilmStorage filmStorage;

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public void updateFilm(Film updatedFilm) {
        validateFilmId(updatedFilm.getId());
        filmStorage.updateFilm(updatedFilm);
    }


    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с указанным ID не найден: " + id));
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public List<Film> getTopFilms(Long count) {
        return filmStorage.getTopFilms(count);
    }

    public void removeFilm(Long id) {
        validateFilmId(id);
        filmStorage.removeFilm(id);
    }

    public void validateFilmId(long filmId) {
        if (filmId <= 0 || !filmStorage.filmExists(filmId)) {
            log.error("Фильм с id {} не найден", filmId);
            throw new NotFoundException("Film with id " + filmId + " not found");
        }
    }
}