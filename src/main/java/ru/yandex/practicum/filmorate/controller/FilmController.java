package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int filmId = 0;
    private static final LocalDate FIRST_MOVIE_EVER = LocalDate.of(1895, 12, 28);

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
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
        filmId++;
        film.setId(filmId);
        films.put(filmId, film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
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
            films.put(film.getId(), film);
            return film;
        } else {
            log.info("Фильма с таким ID не существует {}",film.getId());
            throw new ValidationException("Фильма с таким ID не существует");
        }
    }

    @GetMapping("/films")
    public Collection getFilms() {
        return films.values();
    }
}
