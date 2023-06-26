package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.LinkedHashSet;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    public Genre getGenreById(int genreId) {
        return genreStorage.getGenreById(genreId).orElseThrow(() -> new NotFoundException("Жанр с " +
                "указанным ID не найден: " + genreId));
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    private void validateGenreId(int genreId) {
        if (genreId <= 0 || genreId > getAllGenres().size()) {
            throw new NotFoundException("Invalid genre ID: " + genreId);
        }
    }

    public LinkedHashSet<Genre> getGenresByFilmId(Long filmId) {
        return genreStorage.getGenresByFilmId(filmId);
    }
}