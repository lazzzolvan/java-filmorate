package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.time.LocalDate;
import java.util.List;


@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private static final LocalDate START_RELEASE_DATE = LocalDate.of(1895, 12, 28);


    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
    }

    public Film create(Film film) {
        validate(film);
        return filmStorage.create(film);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film update(Film film) {
        validate(film);
        return filmStorage.update(film);
    }

    public boolean remove(Film film) {
        return filmStorage.remove(film);
    }

    public Film get(Long id) {
        return filmStorage.get(id);
    }

    public boolean addLikeFilm(Long filmID, Long userId) {
        return likeStorage.addLikeFilm(filmID, userId);
    }

    public boolean removeLikeFilm(Long filmId, Long userId) {
        return likeStorage.removeLikeFilm(filmId, userId);
    }

    public List<Film> getFirstCountFilms(Long count) {
        return likeStorage.getFirstCountFilms(count);
    }

    public void validate(Film data) {
        if (data.getReleaseDate().isBefore(START_RELEASE_DATE)) {
            throw new ValidationException("Film release data is invalid");
        }
    }
}
