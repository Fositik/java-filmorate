package ru.yandex.practicum.filmorate.exceptions;

public class FilmNotFoundException  extends RuntimeException {
        public FilmNotFoundException(String message, NotFoundException e) {
            super(message);
        }
    }
