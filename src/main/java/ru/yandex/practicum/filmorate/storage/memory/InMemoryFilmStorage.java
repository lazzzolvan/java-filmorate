package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> filmStorage = new HashMap<>();
    private long generateID;

    @Override
    public Film create(Film film) {
        film.setId(++generateID);
        filmStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(filmStorage.values());
    }

    @Override
    public Film update(Film film) {
        if (!filmStorage.containsKey(film.getId())) {
            throw new DataNotFoundException(String.format("Film %s not found", film));
        }
        filmStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean remove(Film film) {
        if (!filmStorage.containsKey(film.getId())) {
            throw new DataNotFoundException(String.format("Film %s not found", film));
        }
        filmStorage.remove(film.getId());
        return true;
    }

    @Override
    public Film get(Long id) {
        if (!filmStorage.containsKey(id)) {
            throw new DataNotFoundException(String.format("Film %s not found", id));
        }
        return filmStorage.get(id);
    }
}
