package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.idfactory.FilmIdFactory;
import ru.yandex.practicum.filmorate.util.validators.FilmValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
   // private AtomicInteger nextId = new AtomicInteger(1);
private FilmIdFactory filmIdFactory;
    @Override
    public Film addFilm(Film film) throws ValidationException {
        FilmValidator.validateCreate(new ArrayList<>(films.keySet()),film);
        filmIdFactory.setUniqueFilmId(new ArrayList<>(films.keySet()),film);
        //film.setId(nextId.getAndIncrement());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film updatedFilm) throws ValidationException,NotFoundException {
        FilmValidator.validateUpdate(new ArrayList<>(films.keySet()),updatedFilm);
        Film filmToUpdate = films.get(updatedFilm.getId());
        filmToUpdate.setId(updatedFilm.getId());
        filmToUpdate.setName(updatedFilm.getName());
        filmToUpdate.setDescription(updatedFilm.getDescription());
        filmToUpdate.setReleaseDate(updatedFilm.getReleaseDate());
        filmToUpdate.setDuration(updatedFilm.getDuration());
        filmToUpdate.setLikedBy(updatedFilm.getLikedBy());
        return filmToUpdate;
    }

    @Override
    public Film getFilmById(Long id) {
        FilmValidator.validateExist(new ArrayList<>(films.keySet()),id);
        return films.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Long> addLikeToFilm(Long filmId, Long userId) {
        return null;
    }

    @Override
    public List<Long> removeLike(Long filmId, Long userId) {
        return null;
    }

    @Override
    public List<Long> getFilmLikes(Long filmId) {
        return null;
    }

}