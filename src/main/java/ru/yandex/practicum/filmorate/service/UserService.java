package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {

    //  User user;
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public void addFriend(int userId, int friendId) {
        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);
        if (user == null || friend == null) {
            log.error("Не верно указан id одного из пользователей");
            throw new UserNotFoundException("Пользователь не найден");
        }
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
    }

    public void deleteFriend(int userId, int friendId) {
        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
    }

    public Set<User> getCommonFriends(int userId, int friendId) {
        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);
        Set<User> commonFriends = new HashSet<>();
        for (Integer id : user.getFriends()) {
            if (friend.getFriends().contains(id)) {
                commonFriends.add(storage.getUserById(id));
            }
        }
        return commonFriends;

    }

    public Collection<User> getFriends(int id) {
        Collection<User> friendsList = new ArrayList<>();
        User user = storage.getUserById(id);
        for (int friendId : user.getFriends()) {
            friendsList.add(storage.getUserById(friendId));
        }
        return friendsList;
    }
}
