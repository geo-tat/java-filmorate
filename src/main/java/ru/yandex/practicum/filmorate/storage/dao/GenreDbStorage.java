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
import java.util.stream.Collectors;


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
            jdbcTemplate.update(sqlInsert, id, genre.getId());
        }
        film.setGenres(genresNew);
        return film;
    }

    public List<Film> loadGenresForFilm(Collection<Film> films) {
        List<Integer> filmIds = films.stream()
                .map(Film::getId).collect(Collectors.toList());
        Map<Integer, Film> filmMap = films.stream().collect(Collectors.toMap(Film::getId, film -> film));
        String sql = "SELECT fg.genre_id, g.name  " +
                "FROM film_genre AS fg " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "WHERE film_id = ?";
        for (Integer id : filmIds) {
            jdbcTemplate.query(sql, (rs) -> {
                        int genreId = rs.getInt("genre_id");
                        String genreName = rs.getString("name");
                        filmMap.get(id).getGenres().add(Genre.builder().id(genreId).name(genreName).build());
                    }, id
            );

        }
        return new ArrayList<>(films);
    }
}
