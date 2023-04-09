package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Отвечает за операции с фильмами, — добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков.
 */
@Service
public class FilmService {
    private final List<Film> films = new ArrayList<>();

    /** Добавляет лайк к фильму
     *
     * @param id типа int указывает на id фильма
     * @return объект фильма с обновленным количеством лайков
     */
    public Film addLike(int id) {
        Film film = findById(id);
        film.setLikes(film.getLikes() + 1);
        return film;
    }

    /** Удаляет лайк к фильму
     *
     * @param id типа int указывает на id фильма
     * @return объект фильма с обновленным количеством лайков
     */
    public Film removeLike(int id) {
        Film film = findById(id);
        film.setLikes(film.getLikes() - 1);
        return film;
    }

    /** Вывод 10 наиболее популярных фильмов по количеству лайков.
     *
     * @return список из 10 фильмов отсортированных по количеству лайков
     */
    public List<Film> getTop10Films() {
        films.sort(Comparator.comparingInt(Film::getLikes).reversed());
        return films.subList(0, Math.min(films.size(), 10));
    }

    /** Находит фильм по указанному id
     *
     * @param id искомого фильма
     * @return либо искомый фильм, либо сообщение об ошибке (при передаче некорректного id)
     */
    private Film findById(int id) {
        return films.stream()
                .filter(f -> f.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Фильм с id=" + id + " не найден"));
    }
}