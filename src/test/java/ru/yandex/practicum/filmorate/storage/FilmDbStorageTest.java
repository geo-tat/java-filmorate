package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {
    private final FilmController controller;
    private final FilmDbStorage storage;

    @BeforeEach
    void setUp() {
        Film film = Film.builder()
                .name("Iron Man")
                .description("Tony Stark")
                .releaseDate(LocalDate.of(2008, 9, 21))
                .duration(120)
                .mpa(MPA.builder()
                        .id(3)
                        .name("PG-13")
                        .build()).build();

        Film film1 = Film.builder()
                .name("Spider Man")
                .description("Peter Parker")
                .releaseDate(LocalDate.of(2010, 6, 12))
                .duration(124)
                .mpa(MPA.builder()
                        .id(2)
                        .name("PG").build()).build();
        controller.addFilm(film);
        controller.addFilm(film1);
    }

    @Test
    void updateTest() {
        controller.updateFilm(Film.builder()
                .id(1)
                .name("Iron man 2")
                .description("Test")
                .releaseDate(LocalDate.parse("2011-09-01"))
                .duration(80L)
                .mpa(MPA.builder().id(2).name("PG").build())
                .build());
        Optional<Film> filmOptional = Optional.of(storage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Iron man 2")
                                .hasFieldOrPropertyWithValue("description", "Test")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.parse("2011-09-01"))
                                .hasFieldOrPropertyWithValue("duration", 80L)
                );
    }

    @Test
    void getByIdTest() {
        Optional<Film> filmOptional = Optional.of(controller.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Iron Man")
                                .hasFieldOrPropertyWithValue("description", "Tony Stark")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.parse("2008-09-21"))
                                .hasFieldOrPropertyWithValue("duration", 120L)
                );
    }

    @Test
    void getFilmsTest() {
        List<Film> films = new ArrayList<>(controller.getFilms());

        assertThat(films.get(0).getName()).isEqualTo("Iron Man");
        assertThat(films.get(1).getName()).isEqualTo("Spider Man");
    }
}
