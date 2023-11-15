package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    List<Film> getAll();

    Film update(Film film);

    boolean remove(Film film);

    Film get(Long id);
}
