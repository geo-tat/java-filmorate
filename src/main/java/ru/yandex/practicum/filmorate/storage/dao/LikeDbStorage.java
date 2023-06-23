package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.Collection;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
@Slf4j
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper mapper;


    @Override
    public void addLike(int filmID, int userID) {
        String sql = "INSERT INTO film_user_like (film_id, user_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sql, filmID, userID);
    }

    @Override
    public void removeLike(int filmID, int userID) {
        String sql = "DELETE FROM film_user_like " +
                "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmID, userID);
    }

    @Override
    public Collection<Film> getTopFilms(int count) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration,m.mpa_id, m.name " +
                "FROM film AS f " +
                "JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "LEFT JOIN film_genre AS fg ON fg.film_id = f.film_id " +
                "LEFT JOIN genre AS g ON g.genre_id = fg.genre_id " +
                "LEFT JOIN film_user_like AS ful ON ful.film_id = f.film_id " +
                "GROUP BY f.name " +
                "ORDER BY COUNT (ful.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, mapper, count);
    }
}
