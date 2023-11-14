package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.time.LocalDate;


class FillmServiceTest {
    Film film;
    FilmService filmService;
    UserStorage userStorage;
    FilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService((InMemoryFilmStorage) filmStorage, (InMemoryUserStorage) userStorage);
    }

    @Test
    void validateNegative() {
        film = Film.builder().name("Name").description("Decription").releaseDate(LocalDate.of(1800, 1, 1)).duration(100).build();

        Assertions.assertThrows(ValidationException.class, () -> filmService.validate(film));
    }


    @Test
    void validate() {
        film = Film.builder().name("Name").description("Decription").releaseDate(LocalDate.of(2000, 1, 1)).duration(100).build();

        filmService.validate(film);
    }
}