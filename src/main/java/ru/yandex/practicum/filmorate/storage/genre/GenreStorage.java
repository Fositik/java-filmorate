package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    public Optional<Genre> getGenreById(int genreId);

    public List<Genre> getAllGenres();

    LinkedHashSet<Genre> getGenresByFilmId(Long filmId);

}
