package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService {

    private final FilmDbStorage storage;
    private final GenreDbStorage genre;
    private final UserDbStorage user;
    private final LikeDbStorage likeDbStorage;
    private static final LocalDate FIRST_MOVIE_EVER = LocalDate.of(1895, 12, 28);

    @Autowired
    FilmService(FilmDbStorage storage, GenreDbStorage genre, UserDbStorage user, LikeDbStorage likeDbStorage) {
        this.storage = storage;
        this.genre = genre;
        this.user = user;
        this.likeDbStorage = likeDbStorage;
    }


    public void addLike(int filmID, int userID) {
        Film film = storage.getFilmById(filmID);
        User user1 = user.getUserById(userID);
        likeDbStorage.addLike(filmID, userID);
    }


    public void removeLike(int filmID, int userID) {
        Film film = storage.getFilmById(filmID);
        User user1 = user.getUserById(userID);
        likeDbStorage.removeLike(filmID, userID);
    }

    public List<Film> getTopFilms(int count) {
        List<Film> films = new ArrayList<>(likeDbStorage.getTopFilms(count));
        return genre.loadGenresForFilm(films);
    }

    public Film addFilm(Film film) {
        filmValidation(film);
        Film result = storage.addFilm(film);
        return genre.updateGenre(result);
    }

    public Film updateFilm(Film film) {
        filmValidation(film);
        Film updatedFilm = storage.updateFilm(film);
        return genre.updateGenre(updatedFilm);
    }

    public Collection<Film> getFilms() {
        Collection<Film> films = storage.getFilms();
        return genre.loadGenresForFilm(films);
    }

    public Film getFilmById(int id) {
        Film film = storage.getFilmById(id);
        return genre.loadGenresForFilm(List.of(film)).get(0);
    }

    public List<Film> getRecommendations(int id) {
        List<Film> recommendedFilms = likeDbStorage.getRecommendations(id);
        return genre.loadGenresForFilm(recommendedFilms);
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
