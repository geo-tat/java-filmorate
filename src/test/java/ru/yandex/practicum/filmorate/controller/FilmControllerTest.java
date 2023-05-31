package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    FilmController controller;
    @Autowired
    InMemoryFilmStorage storage;

    @Autowired
    public FilmControllerTest(FilmController controller) {
        this.controller = controller;
    }

    @BeforeEach
    void setUp() {
        storage.clear();

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
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(film));
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

    @Test
    public void getFilmByIdTest() {
        // Given
        Film film = new Film("Iron Man", "Marvel Comics", LocalDate.of(2008, 9, 21),
                120);
        controller.addFilm(film);
        // When
        Film result = controller.getFilmById(1);
        // Then
        assertEquals(1, film.getId());
        assertEquals(film, result);
    }

    @Test
    public void getFilmWrongId() {
        // Given

        // When

        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> {
            controller.getFilmById(33);
        });
        // Then
        assertEquals("Фильма с таким ID не существует.", exception.getMessage());
    }

    @Test
    public void addLikeTest() {
        // Given
        Film film = new Film("Iron Man", "Marvel Comics", LocalDate.of(2008, 9, 21),
                120);
        controller.addFilm(film);
        // When
        controller.addLike(1, 1);
        // Then
        assertEquals(1, film.getLikes().size());
    }

    @Test
    public void removeLikeTest() {
        // Given
        Film film = new Film("Iron Man", "Marvel Comics", LocalDate.of(2008, 9, 21),
                120);
        controller.addFilm(film);
        controller.addLike(1, 1);
        // When
        controller.deleteLike(1, 1);
        // Then
        assertEquals(0, film.getLikes().size());
    }

    @Test
    public void getTopPopularFilms() {
        // Given
        Film ironMan = new Film("Iron Man", "Marvel Comics", LocalDate.of(2008, 9, 21),
                120);
        Film spiderMan = new Film("Spider Man", "Marvel Comics", LocalDate.of(20015, 9, 21),
                148);
        controller.addFilm(ironMan);
        controller.addFilm(spiderMan);
        controller.addLike(1, 1);
        controller.addLike(1, 2);
        controller.addLike(2, 4);
        controller.addLike(2, 3);
        controller.addLike(2, 5);
        // When
        List<Film> test = controller.topPopularFilms(2);
        List<Film> result = new ArrayList<>(List.of(spiderMan, ironMan));
        // Then
        assertEquals(result, test);
    }
}
