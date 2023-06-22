package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class RatingMpaController {

    private final MpaService mpaService;

    @GetMapping
    public List<RatingMPA> getAllRatings() {
        List<RatingMPA> ratings = mpaService.getAllRatings();
        log.info("Getting all MPA ratings");
        return ratings;
    }

    @GetMapping("/{id}")
    public RatingMPA getRatingById(@PathVariable Integer id) {
        if (id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Некорректный MPA ID");
        }
        RatingMPA rating = mpaService.getRatingMpaById(id);
        log.info("Получение MPA рейтинга с ID {}", id);
        return rating;
    }
}
