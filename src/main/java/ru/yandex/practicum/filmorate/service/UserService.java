package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class UserService {

    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public void addFriend(int userId, int friendId) {
        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);
        if (user == null || friend == null) {
            throw new UserNotFoundException("Пользователь c Id: " + userId + " не найден.");
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
