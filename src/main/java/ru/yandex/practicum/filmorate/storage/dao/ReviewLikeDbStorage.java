package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
@Slf4j
public class ReviewLikeDbStorage implements ReviewLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLikeToReview(int id, int userId, boolean isLike) {
        String sql = "INSERT INTO review_like_dislike(review_id, user_id, is_like) " +
                "VALUES(?, ?, ?)";
        jdbcTemplate.update(sql, id, userId, isLike);
    }

    @Override
    public void deleteLikeToReview(int id, int userId, boolean isLike) {
        String sql = "DELETE FROM review_like_dislike " +
                "WHERE review_id = ? AND user_id = ? AND is_like = ?";
        jdbcTemplate.update(sql, id, userId, isLike);
    }

}
