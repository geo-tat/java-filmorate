package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import java.util.Collection;
import java.util.HashMap;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int filmId = 0;
    private static final LocalDate FIRST_MOVIE_EVER = LocalDate.of(1895, 12, 28);

    @Override
    public Film addFilm(Film film) {
        filmValidation(film);
        filmId++;
        film.setId(filmId);
        films.put(filmId, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            filmValidation(film);
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
            throw new FilmNotFoundException("Фильма с таким ID не существует.");
        }
    }

    @Override
    public void clear() {
        films.clear();
        filmId = 0;
    }

    private void filmValidation(Film film) {
        if (film.getName().isEmpty()) {
            log.error("Название фильма не указано");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("У фильма очень длинное описание!");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(FIRST_MOVIE_EVER)) {
            log.error("Слишком старый фильм");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 1) {
            log.error("Отрицательная продолжительность фильма!");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
