package ru.yandex.practicum.filmorate.exception;

public class DirectorNotFoundException extends RuntimeException {

    String message;

    public DirectorNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
