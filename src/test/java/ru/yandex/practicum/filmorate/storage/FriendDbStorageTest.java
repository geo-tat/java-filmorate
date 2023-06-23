package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendDbStorageTest {
    private final UserController controller;


    @BeforeEach
    void setUp() {
        User user = new User("andy@gmail.com", "IronMan", "Andrew"
                , LocalDate.of(1990, 4, 22));
        User user1 = new User("gabriel@gmail.com", "Gaby", "Gabriel",
                LocalDate.of(1995, 11, 12));
        User user2 = new User("ladan@gmail.com", "Theodor", "Teo",
                LocalDate.of(1988, 4, 28));
        controller.addUser(user);
        controller.addUser(user1);
        controller.addUser(user2);
    }

    @Test
    public void addAndGetFriendTest() {
        int friendCount = controller.getFriends(1).size();

        assertThat(friendCount).isEqualTo(0);
        controller.addFriend(1, 2);

        int friendCountOne = controller.getFriends(1).size();

        assertThat(friendCountOne).isEqualTo(1);
        controller.deleteFriend(1, 2);
    }

    @Test
    public void deleteFriendTest() {
        //   controller.addFriend(1,2);
        controller.deleteFriend(1, 2);
        controller.deleteFriend(1, 3);
        int friendCount = controller.getFriends(1).size();
        assertThat(friendCount).isEqualTo(0);
    }

    @Test
    public void getCommonFriendsTest() {
        controller.addFriend(1, 2);
        controller.addFriend(1, 3);
        controller.addFriend(2, 3);
        Optional<User> userOptional = controller.getCommonFriends(1, 2).stream().findAny();
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", 3)
                                .hasFieldOrPropertyWithValue("email", "ladan@gmail.com")
                                .hasFieldOrPropertyWithValue("login", "Theodor")
                                .hasFieldOrPropertyWithValue("name", "Teo")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("1988-04-28"))
                );
    }
}
