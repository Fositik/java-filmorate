package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RatingMpaController {

    private final MpaService mpaService;

    @GetMapping
    public List<RatingMPA> getAllRatings() {
        List<RatingMPA> ratings = mpaService.getAllRatings();
        log.info("Получение списка всех рейтингов MPA: {}", ratings);
        return ratings;
    }

    @GetMapping("/{id}")
    public RatingMPA getRatingById(@PathVariable Integer id) {
        RatingMPA rating = mpaService.getRatingMpaById(id);
        log.info("Получение MPA рейтинга с ID {}", id);
        return rating;
    }
}
