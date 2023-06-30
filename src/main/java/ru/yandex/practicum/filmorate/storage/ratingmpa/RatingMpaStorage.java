package ru.yandex.practicum.filmorate.storage.ratingmpa;

import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;
import java.util.Optional;

public interface RatingMpaStorage {

    Optional<RatingMPA> getRatingMpaById(int ratingId);

    List<RatingMPA> getAllRatings();
}

