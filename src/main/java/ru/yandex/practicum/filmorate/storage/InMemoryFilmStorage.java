package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @Override
    public Film addFilm(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        Film filmToUpdate = films.get(updatedFilm.getId());
        if (filmToUpdate == null) {
            throw new ValidationException("Фильм с id=" + updatedFilm.getId() + " не найден");
        }
        filmToUpdate.setName(updatedFilm.getName());
        filmToUpdate.setDescription(updatedFilm.getDescription());
        filmToUpdate.setReleaseDate(updatedFilm.getReleaseDate());
        filmToUpdate.setDuration(updatedFilm.getDuration());
        return filmToUpdate;
    }

    @Override
    public Film getFilmById(int id) {
        Film film = films.get(id);
        if (film == null) {
            throw new ValidationException("Фильм с id=" + id + " не найден");
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
}