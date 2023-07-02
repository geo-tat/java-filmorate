package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorMapper mapper;
    @Override
    public Collection<Director> getDirectors() {
        String sql = "SELECT * FROM director ORDER BY director_id ASC";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public Director getDirectorById(int id) {
        String sql = "SELECT * FROM director WHERE director_id = ?";

        return jdbcTemplate.query(sql, mapper, id)
                .stream()
                .findAny()
                .orElseThrow(() -> new GenreNotFoundException("ID не существует."));
    }

    @Override
    public Director addDirector(Director director) {
        String sql = "INSERT INTO director (name) values(?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        getDirectorById(director.getId());

        String sqlQuery = "UPDATE director SET name = ?  WHERE director_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                director.getName(),
                director.getId()
        );

        return director;
    }

    @Override
    public void deleteDirector(int id) {

    }

    public Film updateDirectorOfFilms(Film film) {

        int filmId = film.getId();
        List<Director> directors = (List<Director>) film.getDirectors();

        String sqlDelete = "DELETE FROM public.film_director WHERE film_id = ? ";
        jdbcTemplate.update(sqlDelete, filmId);

        if (directors == null || directors.size() == 0) {
            film.setDirectors(new ArrayList<>());
            return film;
        }

        String sqlQuery = "MERGE INTO public.film_director (film_id, director_id) values (?, ?)";

        for (Director director : directors) {

            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                        stmt.setInt(1, filmId);
                        stmt.setInt(2, director.getId());
                        return stmt;
                    }
            );
        }

        film.setDirectors(getDirectors(filmId));

        return film;

    }

    List<Director> getDirectors(int film_id) {

        String sql = "SELECT d.director_id , d.name  \n" +
                "FROM public.director AS d\n" +
                "INNER JOIN\n" +
                "public.film_director AS fd ON fd.director_id = d.director_id  AND fd.film_id = ?";

        List<Director> directors = jdbcTemplate.query(sql, (rs, rowNum) -> mapper.mapRow(rs, rowNum), film_id);

        return directors;

    }

    public Collection<Film> updateDirectorOfAllFilms(Collection<Film> films) {
        if (films != null && films.size() != 0) {
            for (Film film : films) {
                film.setDirectors(getDirectors(film.getId()));
            }
        }
        return films;
    }

}
