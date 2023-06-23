package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.Collection;

@RestController
public class MPAController {
    MPAService service;

    @Autowired
    public MPAController(MPAService service) {
        this.service = service;
    }

    @GetMapping("/mpa")
    public Collection<MPA> getMPAList() {
        return service.getList();
    }

    @GetMapping("/mpa/{id}")
    public MPA getMPAById(@PathVariable int id) {
        return service.getMPA(id);
    }
}
