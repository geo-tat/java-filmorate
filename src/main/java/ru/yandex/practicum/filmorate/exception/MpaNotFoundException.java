package ru.yandex.practicum.filmorate.exception;

public class MpaNotFoundException extends RuntimeException {
    String message;

    public MpaNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
