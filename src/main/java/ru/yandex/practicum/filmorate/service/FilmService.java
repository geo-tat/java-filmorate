package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage storage;
    private static final LocalDate FIRST_MOVIE_EVER = LocalDate.of(1895, 12, 28);

    @Autowired
    FilmService(FilmStorage storage) {
        this.storage = storage;
    }

    public void addLike(int filmID, int userID) {
        Film film = storage.getFilmById(filmID);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с Id: " + filmID + " - не найден.");
        }
        film.getLikes().add(userID);
    }

    public void removeLike(int filmID, int userID) {
        Film film = storage.getFilmById(filmID);
        if (film == null || !film.getLikes().contains(userID)) {
            throw new FilmNotFoundException("Фильм с Id: " + filmID + " - не найден.");
        }
        film.getLikes().remove(userID);
    }

    public List<Film> getTopFilms(int count) {
        List<Film> films = new ArrayList<>(storage.getFilms());
        films.sort(Comparator.<Film>comparingInt(film -> film.getLikes().size()).reversed());
        return films.stream().limit(count).collect(Collectors.toList());
    }

    public Film addFilm(Film film) {
        filmValidation(film);
        return storage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        filmValidation(film);
        return storage.updateFilm(film);
    }

    public Collection<Film> getFilms() {
        return storage.getFilms();
    }

    public Film getFilmById(int id) {
        return storage.getFilmById(id);
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
