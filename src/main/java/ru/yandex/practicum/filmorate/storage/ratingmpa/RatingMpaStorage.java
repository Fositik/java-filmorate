package ru.yandex.practicum.filmorate.storage.ratingmpa;

import org.springframework.cache.annotation.Cacheable;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;

public interface RatingMpaStorage {
   // Integer getRatingByName(String rating) throws NotFoundException;


    RatingMPA getRatingMpaById(int ratingId) throws NotFoundException;

    List<RatingMPA> getAllRatings();

 //  void setRatingForFilm(Long filmId, Integer ratingId);
}

