package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.memory.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.memory.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceTest {

    User user;
    UserService userService;
    UserStorage userStorage;

    private final JdbcTemplate jdbcTemplate;
    private FriendStorage friendStorage;

    @BeforeEach
    void setUp() {
        userStorage = new UserDbStorage(jdbcTemplate);
        friendStorage = new FriendDbStorage(jdbcTemplate, userStorage);
        userService = new UserService(userStorage, friendStorage);
    }

    @Test
    void validate() {
        user = User.builder()
                .name("Name")
                .email("email@email.com")
                .login("login")
                .birthday(LocalDate.of(1995, 2, 1))
                .build();

        userService.validate(user);
    }

    @Test
    void validateNegative() {
        user = User.builder()
                .name("")
                .email("email@email.com")
                .login("login")
                .birthday(LocalDate.of(1995, 3, 1))
                .build();

        userService.validate(user);

        assertEquals(user.getName(), user.getLogin());

    }
}