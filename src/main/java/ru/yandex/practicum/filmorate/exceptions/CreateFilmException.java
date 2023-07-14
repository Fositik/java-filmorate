package ru.yandex.practicum.filmorate.exceptions;

public class CreateFilmException extends RuntimeException {
    public CreateFilmException(String message) {
        super(message);
    }

    public CreateFilmException(String message, Throwable cause) {
        super(message, cause);
    }
}
