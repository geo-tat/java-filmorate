package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    boolean deleteReview(int id);

    Review getReviewById(int id);

    Collection<Review> getReviews();

    Collection<Review> getReviewsByFilm(int count, Integer filmId);

}
