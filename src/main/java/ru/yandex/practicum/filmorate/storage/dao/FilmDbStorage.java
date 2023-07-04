package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper mapper;

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO film(name,description, release_date, duration, mpa_id) " +
                "values(?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE film SET " +
                "name = ?, description = ?, release_date = ?, duration = ?,  mpa_id = ? " +
                "WHERE film_id = ?";
        int result = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (result == 0) {
            log.error("Фильма с таким ID не существует: {}", film.getId());
            throw new FilmNotFoundException("Фильма с таким ID не существует");
        } else {
            return film;
        }
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration,m.mpa_id, m.name " +
                "FROM film AS f " +
                "JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "GROUP BY f.film_id";

        return jdbcTemplate.query(sql, mapper);

    }

    @Override
    public Film getFilmById(int id) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration,m.mpa_id, m.name " +
                "FROM film AS f " +
                "JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "WHERE f.film_id = ? " +
                "GROUP BY f.film_id";
        return jdbcTemplate.query(sql, mapper, id)
                .stream()
                .findAny()
                .orElseThrow(() -> new FilmNotFoundException("Фильм c Id: " + id + " не найден."));
    }

    @Override
    public Collection<Film> getFilmOfDirectorSortBy(int directorId, String sortParam) {

        String sql = null;

        if (sortParam.equals("year")) {
            sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration,m.mpa_id, m.name " +
                    "FROM film AS f " +
                    "JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                    "JOIN film_director AS fd ON fd.film_id  = f.film_id " +
                    "WHERE fd.director_id  = ? " +
                    "ORDER BY f.release_date ";

        } else if (sortParam.equals("likes")) {
            sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration,m.mpa_id, m.name, " +
                    "COUNT(ful.FILM_ID) AS rate " +
                    "FROM film AS f " +
                    "JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                    "JOIN film_director AS fd ON fd.film_id  = f.film_id " +
                    "LEFT JOIN film_user_like AS ful ON ful.film_id = f.film_id " +
                    "WHERE fd.director_id  = ? " +
                    "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration,m.mpa_id, m.name " +
                    "ORDER BY rate";

        }

        return jdbcTemplate.query(sql, mapper, directorId);
    }

    public boolean deleteFilmById(int filmId) {
        String sql = "DELETE FROM film WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
        return jdbcTemplate.update(sql, filmId) > 0;
    }

    @Override
    public List<Film> search(String query, List<String> by) {

        if (by.size() == 2) {
            if (by.contains("title") && by.contains("director")) {
                return searchByTitleAndDirector(query);
            } else {
                throw new IncorrectParameterException("Указаны неверные параметры поиска");
            }
        } else if (by.size() == 1) {
            if (by.get(0).equals("title")) {
                return searchByTitle(query);
            } else if (by.get(0).equals("director")) {
                return searchByDirector(query);
            }
        }
        throw new IncorrectParameterException("Отсутствуют параметры поиска");
    }

    private List<Film> searchByTitleAndDirector(String query) {

        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name, d.name, " +
                "COUNT(ful.film_id) AS rate " +
                "FROM film AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "LEFT JOIN film_director AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN director d ON d.director_id = fd.director_id " +
                "LEFT JOIN film_user_like AS ful ON ful.film_id = f.film_id " +
                "WHERE f.film_id IN (SELECT film_id " +
                "                   FROM film_director " +
                "                   WHERE director_id IN (SELECT director_id " +
                "                                       FROM director " +
                "                                       WHERE LOWER(name) LIKE LOWER(?))) " +
                "OR LOWER(f.name) LIKE LOWER(?) " +
                "GROUP BY f.film_id " +
                "ORDER BY rate DESC;";

        return jdbcTemplate.query(sql, mapper, "%" + query + "%", "%" + query + "%");
    }

    private List<Film> searchByTitle(String query) {

        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name, " +
                "COUNT(ful.film_id) AS rate " +
                "FROM film AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "LEFT JOIN film_user_like AS ful ON ful.film_id = f.film_id " +
                "WHERE LOWER(f.name) LIKE LOWER(?) " +
                "GROUP BY f.film_id " +
                "ORDER BY rate DESC;";

        return jdbcTemplate.query(sql, mapper, "%" + query + "%");
    }

    private List<Film> searchByDirector(String query) {

        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name, d.name, " +
                "COUNT(ful.film_id) AS rate " +
                "FROM film AS f " +
                "LEFT JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "LEFT JOIN film_director AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN director d ON d.director_id = fd.director_id " +
                "LEFT JOIN film_user_like AS ful ON ful.film_id = f.film_id " +
                "WHERE f.film_id IN (SELECT film_id " +
                "                   FROM film_director " +
                "                   WHERE director_id IN (SELECT director_id " +
                "                                       FROM director " +
                "                                       WHERE LOWER(name) LIKE LOWER(?))) " +
                "GROUP BY f.film_id " +
                "ORDER BY rate DESC;";

        return jdbcTemplate.query(sql, mapper, "%" + query + "%");
    }
}
