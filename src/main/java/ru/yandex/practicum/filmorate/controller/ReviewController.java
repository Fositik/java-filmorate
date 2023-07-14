package ru.yandex.practicum.filmorate.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        Review createdReview = reviewService.createReview(review);
        log.info("Добавление нового отзыва: {}", review);
        return createdReview;
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        Review updatedReview = reviewService.updateReview(review);
        log.info("Редактирование уже имеющегося отзыва: {}", review);
        return updatedReview;
    }

    @DeleteMapping("/{id}")
    public void removeReview(@PathVariable("id") Long id) {
        log.info("Удаление уже имеющегося отзыва по id: {}", id);
        reviewService.removeReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") Long id) {
        log.info("Получение отзыва по id: {}", id);
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getAllReviews(
            //Этот параметр запроса не является обязательным
            @RequestParam(required = false) Long filmId,
            //Если count не указан, будет использовано значение 10
            @RequestParam(required = false, defaultValue = "10") int count) {
        return reviewService.getAllReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeReview(@PathVariable(name = "id") Long reviewId,
                           @PathVariable(name = "userId") Long userId) {
        reviewService.likeReview(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislikeReview(@PathVariable(name = "id") Long reviewId,
                              @PathVariable(name = "userId") Long userId) {
        reviewService.dislikeReview(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable(name = "id") Long reviewId,
                           @PathVariable(name = "userId") Long userId) {
        reviewService.removeLike(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable(name = "id") Long reviewId,
                              @PathVariable(name = "userId") Long userId) {
        reviewService.removeDislike(reviewId, userId);
    }
}
