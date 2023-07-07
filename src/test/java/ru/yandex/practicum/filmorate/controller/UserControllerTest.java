package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dao.FeedDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private final UserController controller;
    private final FeedDbStorage feedDbStorage;
    private final FilmController filmController;
    private final ReviewController reviewController;

    User user1;
    User user2;
    Film film1;
    Review review1;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(2001, 1, 1))
                .build();
        user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(2002, 1, 1))
                .build();

        film1 = Film.builder()
                .name("Iron Man")
                .description("Tony Stark")
                .releaseDate(LocalDate.of(2008, 9, 21))
                .duration(120)
                .mpa(MPA.builder()
                        .id(3)
                        .name("PG-13")
                        .build()).build();

        review1 = Review.builder()
                .content("Fantastic")
                .isPositive(true)
                .filmId(1)
                .userId(1)
                .build();
    }

    @Test
    public void getFeedTest() {
        int userId = controller.addUser(user1).getId();
        int friendId = controller.addUser(user2).getId();

        int filmId1 = filmController.addFilm(film1).getId();

        controller.addFriend(userId, friendId);
        controller.deleteFriend(userId, friendId);

        filmController.addLike(filmId1, userId);
        filmController.deleteLike(filmId1, userId);

        reviewController.addReview(review1);
        reviewController.updateReview(review1);
        reviewController.deleteReview(1);

        Collection<Feed> feeds = feedDbStorage.getFeed(1);

        assertEquals(7, feeds.size());

        Feed addFriend = feeds.stream()
                .filter(feed -> feed.getEventType() == EventType.FRIEND)
                .findFirst()
                .get();

        assertEquals(friendId, addFriend.getEntityId());

        Feed addLike = feeds.stream()
                .filter(feed -> feed.getEventType() == EventType.LIKE)
                .findFirst()
                .get();

        assertEquals(filmId1, addLike.getEntityId());

        Feed addReview = feeds.stream()
                .filter(feed -> feed.getEventType() == EventType.REVIEW)
                .findFirst()
                .get();

        assertEquals(review1.getReviewId(), addReview.getEntityId());
    }
}