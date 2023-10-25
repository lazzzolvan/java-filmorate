package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends BaseController<Film> {

    private static final LocalDate START_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public void validate(Film data) {
        if (data.getReleaseDate().isBefore(START_RELEASE_DATE)) {
            throw new ValidationException("Film release data is invalid");
        }
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Creating film {}", film);
        return super.create(film);
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("Get all films");
        return super.getAll();
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Updating film {}", film);
        return super.update(film);
    }
}
