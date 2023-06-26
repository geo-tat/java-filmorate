package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface LikeStorage {
    void addLike(int filmID, int userID);

    void removeLike(int filmID, int userID);

    Collection<Film> getTopFilms(int count);
}
