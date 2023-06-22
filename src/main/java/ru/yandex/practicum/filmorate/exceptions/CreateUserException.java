package ru.yandex.practicum.filmorate.exceptions;

public class CreateUserException extends RuntimeException{
    public CreateUserException(String message) {
        super(message);
    }
}
