package ru.yandex.practicum.filmorate.exception;

public class IncorrectParameterException extends RuntimeException {

    private final String message;

    public IncorrectParameterException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
