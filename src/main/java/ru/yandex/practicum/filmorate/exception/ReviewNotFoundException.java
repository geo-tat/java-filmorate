package ru.yandex.practicum.filmorate.exception;

public class ReviewNotFoundException extends RuntimeException {
    String message;

    public ReviewNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
