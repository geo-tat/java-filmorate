package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LikeStorage {
    void addLike(int filmID, int userID);

    void removeLike(int filmID, int userID);

    Collection<Film> getTopFilms(int count, Optional<Integer> genreId, Optional<Integer> year);

    List<Film> getRecommendations(int userId);
}
