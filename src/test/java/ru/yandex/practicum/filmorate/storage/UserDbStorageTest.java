package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDbStorageTest {
    private final UserController controller;
    private final UserDbStorage storage;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("andy@gmail.com")
                .login("IronMan")
                .name("Andrew")
                .birthday(LocalDate.of(1990, 4, 22))
                .build();

        User user1 = User.builder()
                .email("gabriel@gmail.com")
                .login("Gaby")
                .name("Gabriel")
                .birthday(LocalDate.of(1995, 11, 12))
                .build();

        controller.addUser(user);
        controller.addUser(user1);
    }

    @Test
    void shouldUpdateUserTest() {
        User userTest = controller.getUserById(1);
        userTest.setName("Theodor");
        controller.updateUser(userTest);
        Optional<User> userOptional = Optional.of(controller.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("email", "andy@gmail.com")
                                .hasFieldOrPropertyWithValue("login", "IronMan")
                                .hasFieldOrPropertyWithValue("name", "Theodor")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("1990-04-22"))
                );
    }

    @Test
    void shouldReturnUserByIdTest() {
        Optional<User> userOptional = Optional.of(controller.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("email", "andy@gmail.com")
                                .hasFieldOrPropertyWithValue("login", "IronMan")
                                .hasFieldOrPropertyWithValue("name", "Andrew")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("1990-04-22"))
                );
    }

    @Test
    void shouldGetUsersTest() {
        List<User> users = new ArrayList<>(controller.getUsers());

        assertThat(users.get(0).getEmail()).isEqualTo("andy@gmail.com");
        assertThat(users.get(1).getEmail()).isEqualTo("gabriel@gmail.com");
    }

    @Test
    void deleteUserById() {
        // Given

        // When
        controller.deleteUserById(1);
        controller.deleteUserById(2);
        // Then
        assertThat(controller.getUsers()).isEmpty();
    }
}
