package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class UserService {

    private final UserDbStorage storage;
    private final FriendsStorage friendStorage;

    @Autowired
    public UserService(UserDbStorage storage, FriendsStorage friendStorage) {
        this.storage = storage;
        this.friendStorage = friendStorage;
    }

    public User addUser(User user) {
        userValidation(user);
        return storage.addUser(user);
    }

    public User updateUser(User user) {
        userValidation(user);
        return storage.updateUser(user);
    }

    public Collection<User> getUsers() {
        return storage.getUsers();
    }

    public User getUserById(int id) {
        return storage.getUserById(id);
    }

    public boolean deleteUserById(int id) {
        User userToDelete = storage.getUserById(id);
        return storage.deleteUserById(id);
    }

    public void addFriend(int userId, int friendId) {
        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);
        friendStorage.addFriend(user, friend);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);
        friendStorage.deleteFriend(user, friend);
    }

    public Collection<User> getCommonFriends(int userId, int friendId) {
        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);
        return friendStorage.getCommonFriends(userId, friendId);
    }

    public Collection<User> getFriends(int id) {
        User user = storage.getUserById(id);
        return friendStorage.getFriends(id);
    }

    private void userValidation(User user) {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Имя для отображения может быть пустым — в таком случае будет использован логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем!");
            throw new ValidationException("Дата рождения не может быть в будущем!");
        }
    }
}
