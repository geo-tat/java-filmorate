package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
public class FilmController {


    FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        return service.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return service.updateFilm(film);
    }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return service.getFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        return service.getFilmById(id);
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
    public List<Film> topPopularFilms(@RequestParam(defaultValue = "10") int count, @RequestParam Optional<Integer> genreId, @RequestParam Optional<Integer> year) {
        return service.getTopFilms(count, genreId, year);
    }

    @GetMapping("/films/director/{directorId}")
    public Collection<Film> getFilmOfDirectorSortBy(@PathVariable int directorId, @RequestParam(defaultValue = "year") Optional<String> sortBy) {
        String sortParam = sortBy.get();
        return service.getFilmOfDirectorSortBy(directorId, sortParam);
    }

    // GET /users/{id}/recommendations
    @GetMapping("users/{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable int id) {
        return service.getRecommendations(id);
    }

    // Удаляем фильм
    @DeleteMapping("films/{filmId}")
    public boolean deleteFilmById(@PathVariable int filmId) {
       return service.deleteFilmById(filmId);
    }
}
