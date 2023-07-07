package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;


import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmControllerTest {
    private final FilmController controller;
    private final UserDbStorage userDbStorage;

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
    public void getFilmWrongId() {
        // Given

        // When

        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> {
            controller.getFilmById(33);
        });
        // Then
        assertEquals("Фильм c Id: 33 не найден.", exception.getMessage());
    }

    @Test
    public void getCommonFilmsTest() {
        // Given
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(2001, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(2002, 1, 1))
                .build();

        int userId = userDbStorage.addUser(user1).getId();
        int friendId = userDbStorage.addUser(user2).getId();

        int filmId1 = controller.addFilm(film).getId();
        int filmId2 = controller.addFilm(film1).getId();

        controller.addLike(filmId1, userId);
        controller.addLike(filmId1, friendId);
        controller.addLike(filmId2, friendId);

        // When
        Collection<Film> commonFilms = controller.getCommonFilms(userId, friendId);

        // Then
        assertEquals(1, commonFilms.size());
        assertTrue(commonFilms.contains(film));
    }
}
