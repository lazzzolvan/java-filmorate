package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.filmorate.storage.memory.GenreDbStorage.createGenre;
import static ru.yandex.practicum.filmorate.storage.memory.MpaDbStorage.createMpa;

@Component
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public LikeDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage, @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    @Override
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

    @Override
    public List<Film> getFirstCountFilms(Long count) {
        String sqlQuery = "SELECT FILMS.*\n" +
                "FROM FILMS\n" +
                "LEFT JOIN (\n" +
                "    SELECT film_id, COUNT(user_id) AS like_count\n" +
                "    FROM LIKES\n" +
                "    GROUP BY film_id\n" +
                ") AS film_likes ON FILMS.id = film_likes.film_id\n" +
                "ORDER BY COALESCE(like_count, 0) DESC\n" +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::createFilm, count);
        return films;
    }

    @Override
    public boolean removeLikeFilm(Long filmId, Long userId) {
        userStorage.get(userId);
        filmStorage.get(filmId);
        String sqlQuery = "delete from LIKES where user_id = ? and film_id = ?";
        int update = jdbcTemplate.update(sqlQuery, userId, filmId);
        if (update == 0)
            throw new DataNotFoundException(String.format("user with id %s or film with id %s not found", userId, filmId));
        return true;
    }

    Film createFilm(ResultSet rs, int rowNum) throws SQLException {
        String sql = "SELECT g.NAME, g.id \n" +
                "FROM FILM_GENRES fg JOIN GENRES g ON fg.GENRE_ID = g.ID\n" +
                "where fg.film_id = ?";
        List<Genre> genreList = jdbcTemplate.query(sql, (result, rowNum1) ->
                createGenre(result, rowNum1), rs.getInt("id"));
        Set<Genre> genres = new HashSet<>(genreList);

        String sqlQueryByMpa = "SELECT mpa.ID, mpa.NAME\n" +
                "FROM mpa JOIN FILMS ON mpa.ID = films.MPA_ID\n" +
                "WHERE films.ID = ?";
        List<Mpa> mpas = jdbcTemplate.query(sqlQueryByMpa, (result, rowNum1) ->
                createMpa(result, rowNum1), rs.getInt("id"));

        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .rate(rs.getInt("rate"))
                .duration(rs.getInt("duration"))
                .mpa(mpas.get(0))
                .genres(genres)
                .build();
    }
}
