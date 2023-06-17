package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {
    private final UserController controller;
    @Autowired
    private InMemoryUserStorage storage;


    @BeforeEach
    void setUp() {
        storage.clear();
    }

    @Autowired
    public UserControllerTest(UserController controller) {
        this.controller = controller;
    }

    @Test
    public void addUserTest() {
        // Given
        User user1 = new User("andy@gmail.com", "IronMan", "Andrew", LocalDate.of(1990, 4, 22));
        // When
        User result = controller.addUser(user1);
        // Then
        assertEquals(user1, result);
    }

    @Test
    public void testAddUserWithEmptyEmail() {
        // Given
        User user1 = new User("", "IronMan", "Andrew", LocalDate.of(1990, 4, 22));
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addUser(user1);
        });
        // Then
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    public void testAddUserWithEmptyLogin() {
        // Given
        User user1 = new User("andy@gmail.com", "", "Andrew", LocalDate.of(1990, 4, 22));
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addUser(user1);
        });
        // Then
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }


    @Test
    public void testAddUserWithInvalidBBirthday() {
        // Given
        User user1 = new User("andy@gmail.com", "Andy", "Andrew",
                LocalDate.of(2024, 4, 22));
        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addUser(user1);
        });
        // Then
        assertEquals("Дата рождения не может быть в будущем!", exception.getMessage());
    }

    @Test
    public void testUpdateUser() {
        // Given
        User user = new User("andy@gmail.com", "Andy", "Andrew",
                LocalDate.of(1990, 4, 22));
        controller.addUser(user);
        user.setEmail("andy2000@gmail.com");
        // When
        User test = controller.updateUser(user);
        // Then
        assertEquals(user, test);
    }

    @Test
    public void testUpdateNonExistentUser() {
        // Given
        User user = new User("andy@gmail.com", "Andy", "Andrew",
                LocalDate.of(1990, 4, 22));

        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.updateUser(user);
        });
        // Then
        assertEquals("Пользователя с таким ID не существует", exception.getMessage());
    }

    @Test
    public void testGetUser() {
        // Given
        User user = new User("andy@gmail.com", "Andy", "Andrew",
                LocalDate.of(1990, 4, 22));
        User user1 = new User("gabriel@gmail.com", "Gaby", "Gabriel",
                LocalDate.of(1995, 11, 12));
        controller.addUser(user);
        controller.addUser(user1);
        List<User> test = new ArrayList<User>(List.of(user, user1));
        // When
        Collection<User> result = controller.getUsers();
        // Then
        assertArrayEquals(test.toArray(), result.toArray());
    }

    @Test
    public void getUserByIdTest() {
        // Given
        User user = new User("andy@gmail.com", "Andy", "Andrew",
                LocalDate.of(1990, 4, 22));
        controller.addUser(user);
        // When
        User test = controller.getUserById(1);
        // Then
        assertEquals(user, test);
    }

    @Test
    public void getUserWrongId() {
        // Given

        // When
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            controller.getUserById(33);
        });
        // Then
        assertEquals("Пользователь c Id: 33 не найден.", exception.getMessage());
    }

    @Test
    public void addFriend() {
        // Given
        User user = new User("andy@gmail.com", "Andy", "Andrew",
                LocalDate.of(1990, 4, 22));
        User user1 = new User("gabriel@gmail.com", "Gaby", "Gabriel",
                LocalDate.of(1995, 11, 12));
        controller.addUser(user);
        controller.addUser(user1);
        controller.addFriend(1, 2);
        // When
        controller.addFriend(1, 2);

        // Then
        assertEquals(new ArrayList<>(List.of(user)), controller.getFriends(2));
    }

    @Test
    public void deleteFriendTest() {
        // Given
        User user = new User("andy@gmail.com", "Andy", "Andrew",
                LocalDate.of(1990, 4, 22));
        User user1 = new User("gabriel@gmail.com", "Gaby", "Gabriel",
                LocalDate.of(1995, 11, 12));
        controller.addUser(user);
        controller.addUser(user1);
        controller.addFriend(1, 2);
        // When
        controller.deleteFriend(1, 2);
        // Then
        assertEquals(0, controller.getFriends(1).size());
        assertEquals(new ArrayList<>(), controller.getFriends(2));
    }

    @Test
    public void getCommonFriends() {
        // Given
        User user = new User("andy@gmail.com", "Andy", "Andrew",
                LocalDate.of(1990, 4, 22));
        User user1 = new User("gabriel@gmail.com", "Gaby", "Gabriel",
                LocalDate.of(1995, 11, 12));
        User user2 = new User("David@gmail.com", "Davy", "David",
                LocalDate.of(1993, 1, 18));
        controller.addUser(user);
        controller.addUser(user1);
        controller.addUser(user2);
        controller.addFriend(1, 2);
        controller.addFriend(1, 3);
        controller.addFriend(2, 3);
        // When
        Set<User> test = controller.getCommonFriends(1, 2);
        Set<User> result = new HashSet<>(List.of(user2));
        // Then
        assertEquals(result, test);

    }
}