package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
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
                "LEFT JOIN film_genre AS fg ON fg.film_id = f.film_id " +
                "LEFT JOIN genre AS g ON g.genre_id = fg.genre_id " +
                "GROUP BY f.film_id";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public Film getFilmById(int id) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration,m.mpa_id, m.name, g.genre_id, g.name " +
                "FROM film AS f " +
                "JOIN mpa AS m ON m.mpa_id = f.mpa_id " +
                "LEFT JOIN film_genre AS fg ON fg.film_id = f.film_id " +
                "LEFT JOIN genre AS g ON g.genre_id = fg.genre_id " +
                "WHERE f.film_id = ? " +
                "GROUP BY f.film_id, g.genre_id";
        return jdbcTemplate.query(sql, mapper, id)
                .stream()
                .findAny()
                .orElseThrow(() -> new FilmNotFoundException("Фильм c Id: " + id + " не найден."));
    }
}
