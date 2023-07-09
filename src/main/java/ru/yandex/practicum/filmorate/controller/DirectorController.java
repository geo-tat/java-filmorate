package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
public class DirectorController {

    DirectorService service;

    public DirectorController(DirectorService service) {
        this.service = service;
    }

    @GetMapping("/directors")
    public Collection<Director> getFilms() {
        return service.getDirectors();
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable int id) {
        return service.getDirectorById(id);
    }

    @PostMapping("/directors")
    public Director addDirector(@Valid @RequestBody Director director) {
        return service.addDirector(director);
    }

    @PutMapping("/directors")
    public Director updateFilm(@Valid @RequestBody Director director) {
        return service.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    public void deleteDirector(@PathVariable int id) {
        service.deleteDirector(id);
    }

}
