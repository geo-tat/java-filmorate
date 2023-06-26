package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class UserControllerTest {
    private final UserController controller;

    @Autowired
    public UserControllerTest(UserController controller) {
        this.controller = controller;
    }

}