package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage storage;

    @Autowired
    FilmService(FilmStorage storage) {
        this.storage = storage;
    }

    public void addLike(int filmID, int userID) {
        Film film = storage.getFilmById(filmID);
        if (film == null) {
            log.error("Неверно указан id фильма");
            throw new FilmNotFoundException("Фильм не найден");
        }
        film.getLikes().add(userID);
    }

    public void removeLike(int filmID, int userID) {
        Film film = storage.getFilmById(filmID);
        if (film == null || !film.getLikes().contains(userID)) {
            log.error("Неверно указан id фильма");
            throw new FilmNotFoundException("Фильм не найден");
        }
        film.getLikes().remove(userID);
    }

    public List<Film> getTopFilms(int count) {
        List<Film> films = new ArrayList<>(storage.getFilms());
        films.sort(Comparator.comparingInt(film -> film.getLikes().size()));
        Collections.reverse(films);
        return films.stream().limit(count).collect(Collectors.toList());
    }
}
