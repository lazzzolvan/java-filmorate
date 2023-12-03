package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikeStorage {
    boolean addLikeFilm(Long filmID, Long userId);

    boolean removeLikeFilm(Long filmId, Long userId);

    List<Film> getFirstCountFilms(Long count);

}
