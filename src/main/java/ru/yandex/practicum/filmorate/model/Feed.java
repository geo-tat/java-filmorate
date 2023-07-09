package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Feed {
    private int eventId;
    private int userId;
    private long timestamp;
    private int entityId;
    private EventType eventType;
    private Operation operation;
}
