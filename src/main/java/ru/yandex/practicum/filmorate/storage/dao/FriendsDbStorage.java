package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
@Slf4j
public class FriendsDbStorage implements FriendsStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(User user, User friend) {
        String sql = "INSERT INTO user_friend (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, user.getId(), friend.getId());
    }

    @Override
    public void deleteFriend(User user, User friend) {
        String sql = "DELETE FROM user_friend WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, user.getId(), friend.getId());
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int friendId) {
        String sql = "SELECT u.user_id, email, login, name, birthday " +
                "FROM users AS u " +
                "JOIN user_friend AS u1 ON u.user_id = u1.friend_id " +
                "JOIN user_friend AS u2 ON u1.friend_id = u2.friend_id " +
                "WHERE u1.user_id = ? AND u2.user_id = ?";
        return new ArrayList<>(jdbcTemplate.query(sql, new UserMapper(), userId, friendId));
    }

    @Override
    public Collection<User> getFriends(int id) {
        String sql = "SELECT u.user_id, f.friend_id, u.email, u.login, u.name, u.birthday " +
                "FROM user_friend AS f " +
                "LEFT JOIN users AS u ON f.friend_id = u.user_id " +
                "WHERE f.user_id = ? ";
        return new ArrayList<>(jdbcTemplate.query(sql, new UserMapper(), id));
    }
}
