package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review createReview(Review newReview);

    Review updateReview(Review updatedReview);

    boolean removeReview(Long deletedReviewId);

    Optional<Review> getReviewById(Long reviewId);

    List<Review> getAllReviews(Long filmId, int count);

    void likeReview(Long reviewId, Long userId);

    void dislikeReview(Long reviewId, Long userId);

    void removeLike(Long reviewId, Long userId);

    void removeDislike(Long reviewId, Long userId);

    boolean reviewExists(Long reviewId);
}
