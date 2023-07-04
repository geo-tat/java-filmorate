package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;


public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getFilms();

    Film getFilmById(int id);

    Collection<Film> getFilmOfDirectorSortBy(int directorId, String sortParam);

    boolean deleteFilmById(int filmId);

    List<Film> search(String query, List<String> by);

}
