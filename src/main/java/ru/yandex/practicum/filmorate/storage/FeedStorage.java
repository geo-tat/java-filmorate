package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.Collection;

public interface FeedStorage {
    Collection<Feed> getFeed(int userId);

    void addFeed(int userId, int entityId, EventType eventType, Operation operation);
}
