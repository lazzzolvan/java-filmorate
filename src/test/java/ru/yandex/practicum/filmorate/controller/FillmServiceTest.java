package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.memory.*;

import java.time.LocalDate;


@JdbcTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FillmServiceTest {
    Film film;
    FilmService filmService;
    FilmStorage filmStorage;
    LikeStorage likeStorage;
    UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;


    @BeforeEach
    void setUp() {
        userStorage = new UserDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate);
        likeStorage = new LikeDbStorage(jdbcTemplate, userStorage, filmStorage);
        filmService = new FilmService(filmStorage, likeStorage);
    }

    @Test
    void validateNegative() {
        film = Film.builder().name("Name").description("Decription").releaseDate(LocalDate.of(1800, 1, 1)).duration(110).build();

        Assertions.assertThrows(ValidationException.class, () -> filmService.validate(film));
    }


    @Test
    void validate() {
        film = Film.builder().name("Name").description("Decription").releaseDate(LocalDate.of(2000, 1, 1)).duration(100).build();

        filmService.validate(film);
    }
}