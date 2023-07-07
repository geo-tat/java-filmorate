package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.LikeDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LikeDbStorageTest {
    private final FilmController filmController;
    private final UserController userController;
    private final LikeDbStorage likeDbStorage;

    @BeforeEach
    public void setUp() {
        User user = User.builder()
                .email("andy@gmail.com")
                .login("IronMan")
                .name("Andrew")
                .birthday(LocalDate.of(1990, 4, 22))
                .build();
        User user1 = User.builder()
                .email("gabriel@gmail.com")
                .login("Gaby")
                .name("Gabriel")
                .birthday(LocalDate.of(1995, 11, 12))
                .build();
        User user2 = User.builder()
                .email("antonyemail@gmail.com")
                .login("AntonyLogin")
                .name("Anthony")
                .birthday(LocalDate.of(1995, 11, 12))
                .build();
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
        Film film2 = Film.builder()
                .name("Spider Man 2")
                .description("Peter Parker")
                .releaseDate(LocalDate.of(2010, 6, 12))
                .duration(124)
                .mpa(MPA.builder()
                        .id(2)
                        .name("PG").build()).build();
        userController.addUser(user);
        filmController.addFilm(film);
        filmController.addFilm(film1);
        filmController.addFilm(film2);
        userController.addUser(user1);
        userController.addUser(user2);
    }

    @Test
    public void likeTest() {
        filmController.addLike(1, 1);
        filmController.addLike(1, 2);
        filmController.addLike(2, 1);

        List<Film> filmList = filmController.topPopularFilms(2, Optional.empty(), Optional.empty());
        assertThat(filmList.get(0).getName()).isEqualTo("Iron Man");

        filmController.deleteLike(1, 1);
        filmController.deleteLike(1, 2);

        List<Film> filmListTwo = filmController.topPopularFilms(2, Optional.empty(), Optional.empty());
        assertThat(filmListTwo.get(0).getName()).isEqualTo("Spider Man");
    }

    @Test
    public void testRecommendations() {
        filmController.addLike(1, 1);
        filmController.addLike(1, 2);
        filmController.addLike(2, 1);
        filmController.addLike(2, 2);
        filmController.addLike(3, 1);
        filmController.addLike(3, 3);

        List<Film> recommendedFilms = likeDbStorage.getRecommendations(2);
        System.out.println("recommendations for user 2: " + recommendedFilms.toString());
        assertThat(recommendedFilms.get(0).getName()).isEqualTo("Spider Man 2");
    }
}
