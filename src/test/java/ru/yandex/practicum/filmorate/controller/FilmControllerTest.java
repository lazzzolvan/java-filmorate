package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    Film film;
    FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void validateNegative() {
        film = Film.builder()
                .name("Name")
                .description("Decription")
                .releaseDate(LocalDate.of(1800,1,1))
                .duration(100)
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmController.validate(film));
    }


    @Test
    void validate() {
        film = Film.builder()
                .name("Name")
                .description("Decription")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();

        filmController.validate(film);
    }
}