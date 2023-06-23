package ru.yandex.practicum.filmorate.exception;

public class GenreNotFoundException extends RuntimeException {
    String message;

    public GenreNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
