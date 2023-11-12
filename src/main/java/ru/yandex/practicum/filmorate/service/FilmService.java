package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public boolean addLikeFilm(Long filmID, Long userId) {
        if (filmStorage.get(filmID) != null && userStorage.get(userId) != null) {
            filmStorage.get(filmID).addLike(userId);
            return true;
        } else {
            throw new DataNotFoundException("Данный пользователь или фильм не были найдены");
        }
    }

    public boolean removeLikeFilm(Long filmId, Long userId) {
        if (filmStorage.get(filmId) != null && userStorage.get(userId) != null) {
            filmStorage.get(filmId).getUsersByLike().remove(userId);
            return true;
        } else {
            throw new DataNotFoundException("Данный пользователь или фильм не были найдены");
        }
    }

    public List<Film> getFirstCountFilms(Long count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(film -> -1 * film.getUsersByLike().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
