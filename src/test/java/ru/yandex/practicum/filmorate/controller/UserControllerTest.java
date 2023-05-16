package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController();

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
        Collection result = controller.getUsers();
        // Then
        assertArrayEquals(test.toArray(), result.toArray());
    }
}