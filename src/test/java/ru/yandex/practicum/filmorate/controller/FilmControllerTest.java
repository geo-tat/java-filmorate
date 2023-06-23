package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {
    private final FilmController controller;
    private final FilmDbStorage storage;

    Film film;
    Film film1;
    Film oldFilm;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Iron Man")
                .description("Tony Stark")
                .releaseDate(LocalDate.of(2008, 9, 21))
                .duration(120)
                .mpa(MPA.builder()
                        .id(3)
                        .name("PG-13")
                        .build()).build();

        film1 = Film.builder()
                .name("Spider man")
                .description("Peter Paker")
                .releaseDate(LocalDate.of(2010, 6, 12))
                .duration(124)
                .mpa(MPA.builder()
                        .id(2)
                        .name("PG").build()).build();
        oldFilm = Film.builder()
                .name("First")
                .description("Lumier")
                .releaseDate(LocalDate.of(1008, 9, 21))
                .duration(120)
                .mpa(MPA.builder()
                        .id(3)
                        .name("PG-13")
                        .build()).build();
    }

    @Test
    public void addFilmTest() {
        // Given
        // When
        Film test = controller.addFilm(film);
        // Then
        assertEquals(film, test);
    }


    @Test
    public void addFilmWhenOlder28_12_1895() {
        // Given
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addFilm(oldFilm);
        });
        // Then
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", exception.getMessage());
    }


    @Test
    public void updateFilmTest() {
        // Given
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
        film.setId(34);
        // When
        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> {
            controller.updateFilm(film);
        });
        // Then
        assertEquals("Фильма с таким ID не существует", exception.getMessage());
    }

    @Test
    public void getFilmsTest() {
        // Given
        controller.addFilm(film);
        controller.addFilm(film1);
        List<Film> result = new ArrayList<>(List.of(film, film1));
        // When
        Collection test = controller.getFilms();
        // Then
        assertArrayEquals(result.toArray(), test.toArray());
    }

    @Test
    public void getFilmByIdTest() {
        // Given
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
        assertEquals("Фильм c Id: 33 не найден.", exception.getMessage());
    }

}
