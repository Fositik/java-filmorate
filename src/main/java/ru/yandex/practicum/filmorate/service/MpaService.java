package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.ratingmpa.RatingMpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaService {

    private final RatingMpaStorage ratingMpaStorage;


    //    public MpaService (RatingMpaStorage ratingMpaStorage){
//        this.ratingMpaStorage = ratingMpaStorage;
//    }
    public RatingMPA getRatingMpaById(int ratingId) throws NotFoundException {
        return ratingMpaStorage.getRatingMpaById(ratingId).orElseThrow(() -> new NotFoundException("Рейтинг MPA с " +
                "указанным ID не найден: " + ratingId));
    }

    public List<RatingMPA> getAllRatings() {
        return ratingMpaStorage.getAllRatings();
    }
}
