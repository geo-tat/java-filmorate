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

import java.util.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper mapper;

    @Override
    public Collection<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre ORDER BY genre_id ASC";
        return jdbcTemplate.query(sql, new GenreMapper());
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
            return film;
        }
        List<Genre> genres = film.getGenres();
        Map<Integer, Genre> genreMap = new HashMap<>();
        for (Genre genre : genres) {
            genreMap.put(genre.getId(), genre);
        }
        List<Genre> genresNew = new ArrayList<>(genreMap.values());
        for (Genre genre : genresNew) {
            String sqlInsert = "INSERT INTO film_genre (film_id, genre_id) VALUES (?,?) ";
            jdbcTemplate.update(sqlInsert
                    , id
                    , genre.getId());
        }
        film.setGenres(genresNew);
        return film;
    }

    public List<Genre> getGenresByFilmId(int film_id) {
        String sql = "SELECT g.genre_id AS genre_id, g.name " +
                "FROM genre g " +
                "LEFT JOIN film_genre fg ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> result = new ArrayList<>(jdbcTemplate.query(
                sql,
                (rs, num) -> Genre.builder().
                        id(rs.getInt("genre.genre_id"))
                        .name(rs.getString("genre.name"))
                        .build(), film_id)
        );
        result.sort(Comparator.comparingInt(Genre::getId));
        return result;
    }
}
