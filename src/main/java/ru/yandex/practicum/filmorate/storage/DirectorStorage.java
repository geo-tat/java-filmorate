package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface  DirectorStorage {

    Collection<Director> getDirectors();

    Director getDirectorById(int id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);
}
