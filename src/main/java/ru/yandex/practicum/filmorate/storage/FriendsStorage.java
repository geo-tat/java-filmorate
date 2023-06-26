package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendsStorage {
    void addFriend(User id, User friendId);

    void deleteFriend(User id, User friendId);

    Collection<User> getCommonFriends(int userId, int friendId);

    Collection<User> getFriends(int id);
}
