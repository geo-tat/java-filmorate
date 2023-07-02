package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;

@Service
@Slf4j
public class DirectorService {

    private final DirectorStorage storage;

    public DirectorService(DirectorStorage storage) {
        this.storage = storage;
    }

    public Collection<Director> getDirectors() {
        return storage.getDirectors();
    }

    public Director getDirectorById(int id) {
        return storage.getDirectorById(id);
    }

    public Director addDirector(Director director) {
        directorValidation(director);
       return storage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        directorValidation(director);
        return storage.updateDirector(director);
    }

    public void deleteDirector(int id) {
        storage.deleteDirector(id);
    }

    private void directorValidation(Director director) {
        String name = director.getName();
        if (name.isEmpty() || name.trim().isEmpty() || name == null) {
            log.error("Имя директора не указано");
            throw new ValidationException("Название не может быть пустым");
        }
    }

}
