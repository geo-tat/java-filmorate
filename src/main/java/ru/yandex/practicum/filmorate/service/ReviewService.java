package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.ReviewLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
@Slf4j
public class ReviewService {
    private final ReviewDbStorage storage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final ReviewLikeDbStorage likeDbStorage;
    private final FeedStorage feedStorage;

    public Review addReview(Review review) {
        validation(review);
        Review addedReview = storage.addReview(review);
        feedStorage.addFeed(addedReview.getUserId(), addedReview.getReviewId(), EventType.REVIEW, Operation.ADD);
        return addedReview;
    }

    public Review updateReview(Review review) {
        validation(review);
        Review updatedReview = storage.updateReview(review);
        feedStorage.addFeed(updatedReview.getUserId(), updatedReview.getReviewId(), EventType.REVIEW, Operation.UPDATE);
        return updatedReview;
    }

    public boolean deleteReview(int id) {
        Review review = storage.getReviewById(id);
        feedStorage.addFeed(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.REMOVE);
        return storage.deleteReview(id);
    }

    public Review getReviewById(int id) {
        return storage.getReviewById(id);
    }

    public Collection<Review> getReviewsByFilm(int count, Integer filmId) {
        return storage.getReviewsByFilm(count, filmId).stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());

    }

    public void addLikeToReview(int id, int userId) {
        likeDbStorage.addLikeToReview(id, userId, true);
        storage.updateUseful(id, true);
    }

    public void addDislikeToReview(int id, int userId) {
        likeDbStorage.addLikeToReview(id, userId, false);
        storage.updateUseful(id, false);
    }

    public void deleteLikeToReview(int id, int userId) {
        likeDbStorage.deleteLikeToReview(id, userId, true);
        storage.updateUseful(id, false);
    }

    public void deleteDislikeToReview(int id, int userId) {
        likeDbStorage.deleteLikeToReview(id, userId, false);
        storage.updateUseful(id, true);
    }

    private void validation(Review review) {
        if (userDbStorage.getUserById(review.getUserId()) == null) {
            log.error("Пользователя с таким ID не существует: {}", review.getUserId());
        }

        if (filmDbStorage.getFilmById(review.getFilmId()) == null) {
            log.error("Фильма с таким ID не существует: {}", review.getFilmId());
        }
    }
}
