package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController controller;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
    }

    @Test
    public void addFilmTest() {
        // Given
        Film film = new Film("Iron man", "Marvel Comics", LocalDate.of(2008, 9, 21),
                120);
        // When
        Film test = controller.addFilm(film);
        // Then
        assertEquals(film, test);
    }

    @Test
    public void addFilmWhenNoName() {
        // Given
        Film film = new Film("", "Marvel Comics", LocalDate.of(2008, 9, 21),
                120);
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addFilm(film);
        });
        // Then
        assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    public void addFilmWhenDescriptionOver200() {
        // Given
        Film film = new Film("Iron Man", "Marvel Comicsssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                LocalDate.of(2008, 9, 21),
                120);
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addFilm(film);
        });
        // Then
        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    public void addFilmWhenOlder28_12_1895() {
        // Given
        Film film = new Film("Iron Man", "Marvel Comics", LocalDate.of(1008, 9, 21),
                120);
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addFilm(film);
        });
        // Then
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    public void addFilmWhenDurationIsNegative() {
        // Given
        Film film = new Film("Iron Man", "Marvel Comics", LocalDate.of(2008, 9, 21),
                -120);
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addFilm(film);
        });
        // Then
        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    public void updateFilmTest() {
        // Given
        Film film = new Film("Iron Man", "Marvel Comics", LocalDate.of(2008, 9, 21),
                120);
        controller.addFilm(film);
        film.setName("Iron Man 2");
        // When
        Film result = controller.updateFilm(film);
        // Then
        assertEquals(film, result);
    }

    @Test
    public void updateFilmWhenWrongId() {
        // Given
        Film film = new Film("Iron Man", "Marvel Comics", LocalDate.of(2008, 9,
                21), 120);
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.updateFilm(film);
        });
        // Then
        assertEquals("Фильма с таким ID не существует", exception.getMessage());
    }

    @Test
    public void getFilmsTest() {
        // Given
        Film film = new Film("Iron Man", "Marvel Comics", LocalDate.of(2008, 9, 21),
                120);
        Film film2 = new Film("Iron Man 2", "Marvel Comics", LocalDate.of(2010, 9,
                21), 135);
        controller.addFilm(film);
        controller.addFilm(film2);
        List<Film> result = new ArrayList<>(List.of(film, film2));
        // When
        Collection test = controller.getFilms();
        // Then
        assertArrayEquals(result.toArray(), test.toArray());
    }
}
