package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.memory.FilmDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private static final LocalDate START_RELEASE_DATE = LocalDate.of(1895, 12, 28);


    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage, JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
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
        String sqlQuery = "insert into LIKES(user_id, film_id)\n" +
                "values(?, ?)";
        int update = jdbcTemplate.update(sqlQuery, userId, filmID);
        if (update == 0)
            throw new DataNotFoundException(String.format("user with id %s or film with id %s not found", userId, filmID));
        return true;
    }

    public boolean removeLikeFilm(Long filmId, Long userId) {
        userStorage.get(userId);
        filmStorage.get(filmId);
        String sqlQuery = "delete from LIKES where user_id = ? and film_id = ?";
        int update = jdbcTemplate.update(sqlQuery, userId, filmId);
        if (update == 0)
            throw new DataNotFoundException(String.format("user with id %s or film with id %s not found", userId, filmId));
        return true;
    }

    public List<Film> getFirstCountFilms(Long count) {
        String sqlQuery = "SELECT count(film_id), FILM_ID \n" +
                "FROM LIKES \n" +
                "GROUP BY FILM_ID \n" +
                "ORDER BY count(FILM_ID) DESC\n" +
                "LIMIT ?;";
        List<Integer> topFilmsId = jdbcTemplate.query(sqlQuery, this::findPopularFilms, count);
        List<Film> films = new ArrayList<>();
        if (topFilmsId.size() == 0) {
            String sqlQueryFilms = "select id as film_id from FILMS limit ?";
            List<Integer> filmIds = jdbcTemplate.query(sqlQueryFilms, this::findPopularFilms, count);
            for (Integer filmId : filmIds) {
                films.add(get(Long.valueOf(filmId)));
            }
            return films;
        }
        for (Integer currentId : topFilmsId) {
            films.add(get(Long.valueOf(currentId)));
        }
        return films;
/*        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(film -> -1 * film.getUsersByLike().size()))
                .limit(count)
                .collect(Collectors.toList());*/
    }

    public void validate(Film data) {
        if (data.getReleaseDate().isBefore(START_RELEASE_DATE)) {
            throw new ValidationException("Film release data is invalid");
        }
    }

    int findPopularFilms(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("film_id");
    }
}
