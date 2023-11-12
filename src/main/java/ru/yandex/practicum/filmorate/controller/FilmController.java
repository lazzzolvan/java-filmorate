package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        filmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }


    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Creating film {}", film);
        return filmStorage.create(film);
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("Get all films");
        return filmStorage.getAll();
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Updating film {}", film);
        return filmStorage.update(film);
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable Long id) {
        log.info("Get by id film {}", id);
        return filmStorage.get(id);
    }

    @DeleteMapping
    public boolean remove(Film film) {
        log.info("Delete film {}", film);
        return filmStorage.remove(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Add like film {}", filmStorage.get(id));
        return filmService.addLikeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Remove like film {}", filmStorage.get(id));
        return filmService.removeLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10", required = false) Long count) {
        log.info("Get top films {}", count);
        return filmService.getFirstCountFilms(count);
    }
}
