package ru.yandex.practicum.filmorate.exceptions.review;

public class CreateReviewException extends RuntimeException {
    public CreateReviewException(String message) {
        super(message);
    }

    public CreateReviewException(String message, Throwable cause) {
        super(message, cause);
    }
}
