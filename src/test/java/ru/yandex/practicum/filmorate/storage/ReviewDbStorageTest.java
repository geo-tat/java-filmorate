package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReviewDbStorageTest {
    private final FilmController filmController;
    private final UserController userController;
    private final ReviewController reviewController;

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
        Review review = Review.builder()
                .content("Fantastic")
                .isPositive(true)
                .filmId(1)
                .userId(1)
                .build();
        Review review2 = Review.builder()
                .content("Bad")
                .isPositive(false)
                .filmId(2)
                .userId(2)
                .build();
        reviewController.addReview(review);
        reviewController.addReview(review2);
    }

    @Test
    void shouldReturnReviewByIdTest() {
        Optional<Review> reviewOptional = Optional.of(reviewController.getReviewById(1));

        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review ->
                        assertThat(review)
                                .hasFieldOrPropertyWithValue("reviewId", 1)
                                .hasFieldOrPropertyWithValue("content", "Fantastic")
                                .hasFieldOrPropertyWithValue("isPositive", true)
                                .hasFieldOrPropertyWithValue("userId", 1)
                                .hasFieldOrPropertyWithValue("filmId", 1)
                                .hasFieldOrPropertyWithValue("useful", 0)
                );
    }

    @Test
    void shouldUpdateReviewTest() {
        Review reviewTest = reviewController.getReviewById(1);
        reviewTest.setContent("I dont like it");
        reviewTest.setIsPositive(false);
        reviewController.updateReview(reviewTest);
        Optional<Review> reviewOptional = Optional.of(reviewController.getReviewById(1));

        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review ->
                        assertThat(review)
                                .hasFieldOrPropertyWithValue("reviewId", 1)
                                .hasFieldOrPropertyWithValue("content", "I dont like it")
                                .hasFieldOrPropertyWithValue("isPositive", false)
                                .hasFieldOrPropertyWithValue("userId", 1)
                                .hasFieldOrPropertyWithValue("filmId", 1)
                                .hasFieldOrPropertyWithValue("useful", 0)
                );
    }

    @Test
    void shouldGetReviewsTest() {
        List<Review> reviews = new ArrayList<>(reviewController.getReviewsByFilm(10, null));

        assertThat(reviews.get(0).getFilmId()).isEqualTo(1);
        assertThat(reviews.get(1).getFilmId()).isEqualTo(2);
    }

    @Test
    void deleteReviewById() {
        // Given

        // When
        reviewController.deleteReview(1);
        reviewController.deleteReview(2);
        // Then
        assertThat(reviewController.getReviewsByFilm(10, null)).isEmpty();
    }

    @Test
    void addLikeTest() {
        // Given

        // When
        reviewController.addLikeToReview(1, 1);
        reviewController.addLikeToReview(1, 2);
        // Then
        assertThat(reviewController.getReviewById(1).getUseful()).isEqualTo(2);

        reviewController.deleteLikeToReview(1, 2);
        assertThat(reviewController.getReviewById(1).getUseful()).isEqualTo(1);
    }

    @Test
    void addDisLikeTest() {
        // Given
        reviewController.addLikeToReview(1, 1);
        reviewController.addLikeToReview(1, 2);
        // When
        reviewController.addDislikeToReview(2, 1);
        // Then
        assertThat(reviewController.getReviewById(2).getUseful()).isEqualTo(-1);
    }
}
