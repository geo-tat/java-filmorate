package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;


@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper mapper;

    @Override
    public Collection<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre ORDER BY genre_id ASC";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public Genre getGenreById(int id) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";

        return jdbcTemplate.query(sql, mapper, id)
                .stream()
                .findAny()
                .orElseThrow(() -> new GenreNotFoundException("ID не существует."));
    }

    public Film updateGenre(Film film) {

        int id = film.getId();
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            film.setGenres(new ArrayList<>());
            return film;
        }

        String sqlQuery = "MERGE INTO film_genre (film_id, genre_id) values (?, ?)";

        List<Genre> genres = (List<Genre>) film.getGenres();

        for (Genre genre : genres) {

            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                        stmt.setInt(1, id);
                        stmt.setInt(2, genre.getId());
                        return stmt;
                    }
            );
        }

        film.setGenres(getGenres(id));

        return film;

    }

    public List<Film> loadGenresForFilm(Collection<Film> films) {
        Map<Integer, Film> filmMap = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        String sql = "SELECT fg.film_id, fg.genre_id, g.name " +
                "FROM film_genre AS fg " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id IN (" + String.join(",", Collections.nCopies(films.size(), "?")) + ")";
        jdbcTemplate.query(sql, (rs) -> {
            filmMap.get(rs.getInt("film_id")).getGenres().add(Genre.builder()
                    .id(rs.getInt("genre_id"))
                    .name(rs.getString("name"))
                    .build());
        }, filmMap.keySet().toArray());
        return new ArrayList<>(films);
    }

    List<Genre> getGenres(int film_id) {

        String sql = "SELECT g.genre_id , g.name  \n" +
                "FROM genre AS g\n" +
                "INNER JOIN\n" +
                "film_genre AS fgs ON fgs.genre_id = g.genre_id  AND fgs.film_id = ?";

        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> mapper.mapRow(rs, rowNum), film_id);

        return genres;
    }
}
