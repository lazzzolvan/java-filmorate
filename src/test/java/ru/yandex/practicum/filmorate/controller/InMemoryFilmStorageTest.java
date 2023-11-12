package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryFilmStorage;

import java.time.LocalDate;


class InMemoryFilmStorageTest {
    Film film;
    InMemoryFilmStorage inMemoryFilmStorage;

    @BeforeEach
    void setUp() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
    }

    @Test
    void validateNegative() {
        film = Film.builder().name("Name").description("Decription").releaseDate(LocalDate.of(1800, 1, 1)).duration(100).build();

        Assertions.assertThrows(ValidationException.class, () -> inMemoryFilmStorage.validate(film));
    }


    @Test
    void validate() {
        film = Film.builder().name("Name").description("Decription").releaseDate(LocalDate.of(2000, 1, 1)).duration(100).build();

        inMemoryFilmStorage.validate(film);
    }
}