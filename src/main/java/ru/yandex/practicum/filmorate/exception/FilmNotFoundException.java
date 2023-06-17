package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends RuntimeException {
    String message;

    public FilmNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
