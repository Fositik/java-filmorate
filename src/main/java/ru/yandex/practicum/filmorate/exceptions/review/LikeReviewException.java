package ru.yandex.practicum.filmorate.exceptions.review;

public class LikeReviewException extends RuntimeException {
    public LikeReviewException(String message) {
        super(message);
    }

    public LikeReviewException(String message, Throwable cause) {
        super(message, cause);
    }
}
