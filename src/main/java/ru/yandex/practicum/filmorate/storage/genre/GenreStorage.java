package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    public Genre getGenreById(int genreId);

    public List<Genre> getAllGenres();

    List<Genre> getGenresByFilmId(Long filmId);

}
