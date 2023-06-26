package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.Collection;

@Service
@Slf4j
public class MPAService {
    private final MPAStorage storage;

    @Autowired
    public MPAService(MPAStorage storage) {
        this.storage = storage;
    }

    public Collection<MPA> getList() {
        return storage.getList();
    }

    public MPA getMPA(int id) {
        return storage.getMPA(id);
    }
}
