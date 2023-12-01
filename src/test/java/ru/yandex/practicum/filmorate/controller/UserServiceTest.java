package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
class UserServiceTest {

    User user;
    UserService userService;
    UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService((InMemoryUserStorage) userStorage, jdbcTemplate);
    }

    @Test
    void validate() {
        user = User.builder()
                .name("Name")
                .email("email@email.com")
                .login("login")
                .birthday(LocalDate.of(1995, 3, 1))
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