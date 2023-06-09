package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.dao.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FilmService {

    private final FilmDbStorage storage;
    private final GenreDbStorage genre;
    private final UserDbStorage user;
    private final LikeDbStorage likeDbStorage;
    private final DirectorDbStorage director;
    private final FeedDbStorage feedDbStorage;
    private static final LocalDate FIRST_MOVIE_EVER = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmDbStorage storage, GenreDbStorage genre, UserDbStorage user, LikeDbStorage likeDbStorage, DirectorDbStorage director, FeedDbStorage feedDbStorage) {
        this.storage = storage;
        this.genre = genre;
        this.user = user;
        this.likeDbStorage = likeDbStorage;
        this.director = director;
        this.feedDbStorage = feedDbStorage;
    }

    public void addLike(int filmID, int userID) {
        likeDbStorage.addLike(storage.getFilmById(filmID).getId(), user.getUserById(userID).getId());
        feedDbStorage.addFeed(userID, filmID, EventType.LIKE, Operation.ADD);
    }

    public void removeLike(int filmID, int userID) {
        likeDbStorage.removeLike(storage.getFilmById(filmID).getId(), user.getUserById(userID).getId());
        feedDbStorage.addFeed(userID, filmID, EventType.LIKE, Operation.REMOVE);
    }

    public List<Film> getTopFilms(int count, Optional<Integer> genreId, Optional<Integer> year) {
        Collection<Film> films = likeDbStorage.getTopFilms(count, genreId, year);
        director.updateDirectorOfAllFilms(films);
        return genre.loadGenresForFilm(films);
    }

    public Film addFilm(Film film) {
        filmValidation(film);
        Film result = storage.addFilm(film);
        result = genre.updateGenre(result);
        return director.updateDirectorOfFilms(result);
    }

    public Film updateFilm(Film film) {
        filmValidation(film);
        Film updatedFilm = storage.updateFilm(film);
        updatedFilm = genre.updateGenre(updatedFilm);
        return director.updateDirectorOfFilms(updatedFilm);
    }

    public Collection<Film> getFilms() {
        Collection<Film> films = storage.getFilms();
        films = genre.loadGenresForFilm(films);
        return director.updateDirectorOfAllFilms(films);
    }

    public Film getFilmById(int id) {
        Film film = storage.getFilmById(id);
        film = genre.loadGenresForFilm(List.of(film)).get(0);
        film.setDirectors(director.getDirectors(film.getId()));
        return film;
    }

    public Collection<Film> getRecommendations(int id) {
        Collection<Film> recommendedFilms = likeDbStorage.getRecommendations(id);
        return director.updateDirectorOfAllFilms(genre.loadGenresForFilm(recommendedFilms));
    }

    public boolean deleteFilmById(int filmId) {
        return storage.deleteFilmById(storage.getFilmById(filmId).getId());
    }

    public Collection<Film> search(String query, List<String> by) {
        Collection<Film> films = storage.search(query, by);
        return director.updateDirectorOfAllFilms(genre.loadGenresForFilm(films));
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

    public Collection<Film> getFilmOfDirectorSortBy(int directorId, String sortParam) {
        director.getDirectorById(directorId);
        Collection<Film> films = storage.getFilmOfDirectorSortBy(directorId, sortParam);
        films = genre.loadGenresForFilm(films);
        return director.updateDirectorOfAllFilms(films);
    }

    public Collection<Film> getCommonFilms(int userId, int friendId) {
        Collection<Film> films = storage.getCommonFilms(userId, friendId);
        films = genre.loadGenresForFilm(films);
        return director.updateDirectorOfAllFilms(films);
    }
}
