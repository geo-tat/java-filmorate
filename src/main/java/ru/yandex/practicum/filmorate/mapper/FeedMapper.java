package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class FeedMapper implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(rs.getInt("event_id"))
                .userId(rs.getInt("user_id"))
                .timestamp(rs.getTimestamp("time_stamp").toInstant().toEpochMilli())
                .entityId(rs.getInt("entity_id"))
                .eventType((EventType.valueOf(rs.getString("event_type"))))
                .operation((Operation.valueOf((rs.getString("operation")))))
                .build();
    }
}
