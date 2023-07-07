package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
@Slf4j
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper mapper;


    @Override
    public void addLike(int filmID, int userID) {
        String sql = "MERGE INTO film_user_like (film_id, user_id) " +
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
    public Collection<Film> getTopFilms(int count, Optional<Integer> genreId, Optional<Integer> year) {

        int genreParam;
        if (genreId.equals(Optional.empty())) {
            genreParam = 0;
        } else {
            genreParam = genreId.get();
        }

        int yearParam;
        if (year.equals(Optional.empty())) {
            yearParam = 0;
        } else {
            yearParam = year.get();
        }

        if (genreParam == 0 && yearParam == 0) {
            String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration,m.mpa_id, m.name " +
                    "FROM film AS f " +
                    "JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                    "LEFT JOIN film_user_like AS ful ON ful.film_id = f.film_id " +
                    "GROUP BY f.name, f.film_id " +
                    "ORDER BY COUNT (ful.user_id) DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sql, mapper, count);
        }

        if (genreParam != 0 && yearParam == 0) {
            String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration,m.mpa_id, m.name " +
                    "FROM film AS f " +
                    "JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                    "JOIN film_genre AS fg ON fg.film_id = f.film_id " +
                    "LEFT JOIN film_user_like AS ful ON ful.film_id = f.film_id " +
                    "WHERE fg.genre_id = ? " +
                    "GROUP BY f.name, f.film_id " +
                    "ORDER BY COUNT (ful.user_id) DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sql, mapper, genreParam, count);
        }

        if (genreParam == 0 && yearParam != 0) {
            String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.name " +
                    "FROM film AS f " +
                    "JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                    "LEFT JOIN film_user_like AS ful ON ful.film_id = f.film_id " +
                    "WHERE YEAR(f.release_date) = ? " +
                    "GROUP BY f.name, f.film_id " +
                    "ORDER BY COUNT (ful.user_id) DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sql, mapper, yearParam, count);
        }

        if (genreParam != 0 && yearParam != 0) {
            String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration,m.mpa_id, m.name " +
                    "FROM film AS f " +
                    "JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                    "JOIN film_genre AS fg ON fg.film_id = f.film_id " +
                    "LEFT JOIN film_user_like AS ful ON ful.film_id = f.film_id " +
                    "WHERE fg.genre_id = ? AND YEAR(f.release_date) = ? " +
                    "GROUP BY f.name, f.film_id " +
                    "ORDER BY COUNT (ful.user_id) DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sql, mapper, genreParam, yearParam, count);
        }

        return null;

    }

    @Override
    public List<Film> getRecommendations(int userId) {
        //Найти все фильмы, которым поставил лайк пользователь с максимальным количеством пересечений по лайкам.
        String sqlOtherUserFilms = "SELECT f.film_id, f.name, f.description, f.release_date, " +
                "f.duration, m.mpa_id, m.name " +
                "FROM film AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "WHERE f.film_id IN (SELECT film_id " +
                                    "FROM film_user_like " +
                                    "WHERE user_id IN (SELECT u2.user_id " +
                                                "FROM film_user_like u1, film_user_like u2 " +
                                                "WHERE u1.film_id = u2.film_id and " +
                                                "u1.user_id <> u2.user_id and u1.user_id = ? " +
                                                "GROUP BY u2.user_id " +
                                                "LIMIT 1) " +
                "ORDER BY film_id);";

        List<Film> filmLikedByOtherUser = jdbcTemplate.query(sqlOtherUserFilms, mapper, userId);

        //найти все лайки текущего пользователя
        String sqlCurrentUserFilms = "SELECT ful.film_id, f.name, f.description, " +
                "f.release_date, f.duration, m.mpa_id, m.name " +
                "FROM film_user_like AS ful " +
                "JOIN film AS f ON f.film_id = ful.film_id " +
                "JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "WHERE ful.user_id = ? " +
                "ORDER BY film_id;";

        List<Film> filmsLikedByCurrentUser = jdbcTemplate.query(sqlCurrentUserFilms, mapper, userId);

        // Определить фильмы, которые один пролайкал, а другой нет.
        List<Film> recommendedList = new ArrayList<>();
        for (Film film : filmLikedByOtherUser) {
            if (!filmsLikedByCurrentUser.contains(film)) {
                recommendedList.add(film);
            }
        }

        return recommendedList;
    }
}
