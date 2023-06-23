package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.LikeDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikeDbStorageTest {
    private final FilmController filmController;
    private final UserController userController;
    private final LikeDbStorage likeDbStorage;

    @BeforeEach
    public void setUp() {
        User user = new User("andy@gmail.com", "IronMan", "Andrew"
                , LocalDate.of(1990, 4, 22));
        User user1 = new User("gabriel@gmail.com", "Gaby", "Gabriel",
                LocalDate.of(1995, 11, 12));
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
        userController.addUser(user);
        filmController.addFilm(film);
        filmController.addFilm(film1);
        userController.addUser(user1);
    }

    @Test
    public void LikeTest() {
        filmController.addLike(1, 1);
        filmController.addLike(1, 2);
        filmController.addLike(2, 1);

        List<Film> filmList = filmController.topPopularFilms(2);
        assertThat(filmList.get(0).getName()).isEqualTo("Iron Man");

        filmController.deleteLike(1, 1);
        filmController.deleteLike(1, 2);

        List<Film> filmListTwo = filmController.topPopularFilms(2);
        assertThat(filmListTwo.get(0).getName()).isEqualTo("Spider Man");

    }

}
