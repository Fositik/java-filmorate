package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmLikeService;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/films/{id}/like/{userId}")
@Slf4j
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmLikeController {
    private final FilmLikeService filmLikeService;

    @PutMapping
    public void addLikeToFilm(@Positive @PathVariable(name = "id") long id,
                              @Positive @PathVariable(name = "userId") long userId) {
        log.info("Добавление лайка к фильму под id: {} от пользователя с id: {}", id, userId);

        filmLikeService.addLikeToFilm(id, userId);
    }

    @DeleteMapping
    public void removeLike(@PathVariable(name = "id") long id,
                           @PathVariable(name = "userId") long userId) {
        log.info("Удаление лайка с фильма под id: {} от пользователя с id: {}", id, userId);

        filmLikeService.removeLike(id, userId);
    }
}
