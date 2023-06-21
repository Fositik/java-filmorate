package ru.yandex.practicum.filmorate.storage.genre;


import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
  //  public void deleteAllGenresById(int filmId);

    public Genre getGenreById(int genreId);

    public List<Genre> getAllGenres();


    List<Genre> getGenresByFilmId(Long filmId);

}
