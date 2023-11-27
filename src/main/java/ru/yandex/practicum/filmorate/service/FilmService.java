package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate START_RELEASE_DATE = LocalDate.of(1895, 12, 28);


    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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
        userStorage.get(userId);
        filmStorage.get(filmID).addLike(userId);
        return true;
    }

    public boolean removeLikeFilm(Long filmId, Long userId) {
        userStorage.get(userId);
        filmStorage.get(filmId).getUsersByLike().remove(userId);
        return true;
    }

    public List<Film> getFirstCountFilms(Long count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(film -> -1 * film.getUsersByLike().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public void validate(Film data) {
        if (data.getReleaseDate().isBefore(START_RELEASE_DATE)) {
            throw new ValidationException("Film release data is invalid");
        }
    }
}
