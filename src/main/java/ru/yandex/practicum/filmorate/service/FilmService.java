package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.validators.FilmValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
   // private final Map<Integer, Set<Integer>> likedFilmsByUser;

    //Мапа для хранения фильмов понравившихся пользователю <ID пользователя, Set<ID понравившихся фильмов>>
    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
     //   this.likedFilmsByUser = new HashMap<>();
    }

    public Film addFilm(Film film) throws ValidationException, NotFoundException {
        filmStorage.addFilm(film);
        return film;
    }

    public Film updateFilm(Film updatedFilm) throws ValidationException, NotFoundException {
        filmStorage.updateFilm(updatedFilm);
        return updatedFilm;
    }

    public Film getFilmById(Long id) throws NotFoundException {
        Film film = filmStorage.getFilmById(id);
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> filmList = filmStorage.getAllFilms();
        return filmList;
    }

    public List<Film> getTopFilmsByLikes(Integer count) {
        if (count == null) {
            //Если значение count не было передано, по умолчанию оно равно 10
            count = 10;
        }
        List<Film> popular = filmStorage.getAllFilms();
        if (popular.size() <= 1) {
            return popular;
        } else {
            return popular.stream()
                    .sorted((film1, film2) -> {
                        int result = Integer.compare(
                                filmStorage.getFilmLikes(film1.getId()).size(),
                                filmStorage.getFilmLikes(film2.getId()).size()
                        );
                        result = -1 * result;
                        return result;
                    })
                    .limit(count)
                    .collect(Collectors.toList());
        }

    }

//    public List<Integer> addLike(Integer filmId, Integer userId) throws NotFoundException{
//        List<Integer> likedBy =
//        Film film = filmStorage.getFilmById(filmId);
//        Set<Integer> likedUsers = likedFilmsByUser.computeIfAbsent(filmId, k -> new HashSet<>());
//
//        film.setLikes(film.getLikes() + 1);
//        likedUsers.add(userId);
//        filmStorage.updateFilm(film);
//    }

    public List<Long> addLikeToFilm(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        List<Long> likedBy = film.getLikedBy();
        FilmValidator.validateLike(likedBy, filmId);
        likedBy.add(userId);
        filmStorage.updateFilm(film);
        return likedBy;
    }

    public List<Long> removeLike(long filmId, long userId) throws NotFoundException {
        Film film = filmStorage.getFilmById(filmId);
        filmStorage.removeLike(filmId, userId);
        return filmStorage.getFilmLikes(film.getId());
    }
}