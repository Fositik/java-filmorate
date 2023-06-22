package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.ratingmpa.RatingMpaStorage;

import java.util.List;

@Service
public class MpaService {

    private final RatingMpaStorage ratingMpaStorage;

    public MpaService(RatingMpaStorage ratingMpaStorage) {
        this.ratingMpaStorage = ratingMpaStorage;
    }

    public RatingMPA getRatingMpaById(int ratingId) throws NotFoundException {
        validateMpaId(ratingId);
        return ratingMpaStorage.getRatingMpaById(ratingId);
    }

    public List<RatingMPA> getAllRatings() {
        return ratingMpaStorage.getAllRatings();
    }

    private void validateMpaId(int mpaId) {
        if (mpaId <= 0 || mpaId > getAllRatings().size()) {
            throw new NotFoundException("Invalid genre ID: " + mpaId);
        }
    }


}
