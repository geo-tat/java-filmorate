package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.Collection;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
@Slf4j
public class MpaDbStorage implements MPAStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaMapper mapper;

    @Override
    public Collection<MPA> getList() {
        String sql = "SELECT * FROM mpa ORDER BY mpa_id ASC";

        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public MPA getMPA(int id) {
        String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        return jdbcTemplate.query(sql, mapper, id)
                .stream()
                .findAny()
                .orElseThrow(() -> new MpaNotFoundException("Id не существует."));

    }
}
