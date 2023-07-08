package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FeedMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FeedMapper feedMapper;

    @Override
    public Collection<Feed> getFeed(int userId) {
        String sql = "SELECT * FROM feed AS f WHERE user_id = ?";
        return jdbcTemplate.query(sql, feedMapper, userId);
    }

    @Override
    public void addFeed(int userId, int entityId, EventType eventType, Operation operation) {
        String sql = "INSERT INTO feed (user_id, time_stamp, entity_id, event_type, operation) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, userId, Timestamp.from(Instant.now()), entityId, eventType.name(), operation.name());
    }
}
