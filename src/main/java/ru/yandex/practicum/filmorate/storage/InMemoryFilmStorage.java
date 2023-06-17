package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int filmId = 0;

    @Override
    public Film addFilm(Film film) {
        filmId++;
        film.setId(filmId);
        films.put(filmId, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            log.error("Фильма с таким ID не существует: {}", film.getId());
            throw new ValidationException("Фильма с таким ID не существует");
        }
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();

    }

    @Override
    public Film getFilmById(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            log.error("Фильма с таким ID не существует.");
            throw new FilmNotFoundException("Фильм c Id: " + id + " не найден.");
        }
    }

    public void clear() {
        films.clear();
        filmId = 0;
    }
}
