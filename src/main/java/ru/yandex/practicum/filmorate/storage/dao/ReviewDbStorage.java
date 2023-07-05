package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewMapper mapper;

    @Override
    public Review addReview(Review review) {
        String sql = "INSERT INTO reviews(content,is_positive, user_id, film_id) " +
                "values(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"review_id"});
            statement.setString(1, review.getContent());
            statement.setBoolean(2, review.isPositive);
            statement.setInt(3, review.getUserId());
            statement.setInt(4, review.getFilmId());
            return statement;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET " +
                "content = ?, is_positive = ? " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.isPositive,
                review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public boolean deleteReview(int id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public Review getReviewById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";

        return jdbcTemplate.query(sql, mapper, id).stream()
                .findAny()
                .orElseThrow(() -> new ReviewNotFoundException("Отзыв c Id: " + id + " не найден."));
    }

    @Override
    public Collection<Review> getReviews() {
        String sql = "SELECT * FROM reviews";

        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public Collection<Review> getReviewsByFilm(int count, Integer filmId) {
        if (filmId == null) {
            String sql = "SELECT * FROM reviews " +
                    "LIMIT ?";
            return jdbcTemplate.query(sql, mapper, count);
        } else {
            String sql = "SELECT * FROM reviews " +
                    "WHERE film_id = ? " +
                    "LIMIT ?";
            return jdbcTemplate.query(sql, mapper, filmId, count);
        }
    }

    public void updateUseful(int reviewId, boolean isLike) {
        if (isLike) {
            String sqlInc = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
            jdbcTemplate.update(sqlInc, reviewId);
        } else {
            String sqlDec = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
            jdbcTemplate.update(sqlDec, reviewId);
        }
    }
}
