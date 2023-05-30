package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends RuntimeException {
    String message;

    public UserNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
