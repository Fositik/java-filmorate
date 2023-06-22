package ru.yandex.practicum.filmorate.storage.ratingmpa;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;

public interface RatingMpaStorage {

    RatingMPA getRatingMpaById(int ratingId) throws NotFoundException;

    List<RatingMPA> getAllRatings();
}

