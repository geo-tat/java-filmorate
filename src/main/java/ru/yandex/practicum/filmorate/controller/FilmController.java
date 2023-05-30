package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.*;

@RestController
public class FilmController {
    @Autowired
    FilmStorage storage;
    @Autowired
    FilmService service;

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        return storage.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return storage.updateFilm(film);
    }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return storage.getFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        return storage.getFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        service.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> topPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return service.getTopFilms(count);
    }

}
