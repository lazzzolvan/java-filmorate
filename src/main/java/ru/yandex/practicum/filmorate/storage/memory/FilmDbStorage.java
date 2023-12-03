package ru.yandex.practicum.filmorate.storage.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static ru.yandex.practicum.filmorate.storage.memory.GenreDbStorage.createGenre;
import static ru.yandex.practicum.filmorate.storage.memory.MpaDbStorage.createMpa;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("rate", film.getRate());
        parameters.put("duration", film.getDuration());
        parameters.put("mpa_id", film.getMpa().getId());

        Number generatedId = simpleJdbcInsert.executeAndReturnKey(parameters);

        film.setId(generatedId.longValue());

        if (!film.getGenres().isEmpty()) {

            String sqlQuery = "insert into FILM_GENRES (FILM_ID, GENRE_ID)\n" +
                    "values(?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            }
        }

        return get(film.getId());
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "select * from FILMS";
        return jdbcTemplate.query(sqlQuery, this::createFilm);
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update FILMS\n" +
                "set name = ?, release_date = ?, description = ?, duration = ?, rate = ?, mpa_id = ?\n" +
                "where id = ?";
        int update = jdbcTemplate.update(sqlQuery, film.getName(), film.getReleaseDate(), film.getDescription(), film.getDuration(),
                film.getRate(), film.getMpa().getId(), film.getId());
        if (update != 1)
            throw new DataNotFoundException(String.format("film with id %s not single", film.getId()));

        if (film.getGenres().isEmpty()) {
            String sqlQueryCountGenresByFilm = "SELECT count(genre_id)\n" +
                    "FROM FILM_GENRES\n" +
                    "WHERE FILM_ID = ?";
            List<Integer> countGenre = jdbcTemplate.query(sqlQueryCountGenresByFilm, this::findCountGenresByFilm, film.getId());
            if (countGenre.get(0) != 0) {
                String sqlQueryByGenres = "delete from FILM_GENRES\n" +
                        "where film_id = ?";
                update = jdbcTemplate.update(sqlQueryByGenres, film.getId());
                if (update == 0)
                    throw new DataNotFoundException(String.format("film with id %s not single", film.getId()));
            }
        } else {
            String sqlQueryCountGenresByFilm = "SELECT count(genre_id)\n" +
                    "FROM FILM_GENRES\n" +
                    "WHERE FILM_ID = ?";
            List<Integer> countGenre = jdbcTemplate.query(sqlQueryCountGenresByFilm, this::findCountGenresByFilm, film.getId());
            if (countGenre.get(0) == 0 && !film.getGenres().isEmpty()) {
                String sqlQueryGenreInsert = "insert into FILM_GENRES (FILM_ID, GENRE_ID)\n" +
                        "values(?, ?)";
                for (Genre genre : film.getGenres()) {
                    jdbcTemplate.update(sqlQueryGenreInsert, film.getId(), genre.getId());
                }
            } else if (countGenre.get(0) != film.getGenres().size()) {
                String sqlQueryByGenresDelete = "delete from FILM_GENRES\n" +
                        "where film_id = ?";
                update = jdbcTemplate.update(sqlQueryByGenresDelete, film.getId());
                if (update == 0)
                    throw new DataNotFoundException(String.format("film with id %s not single", film.getId()));

                String sqlQueryGenreInsert = "insert into FILM_GENRES (FILM_ID, GENRE_ID)\n" +
                        "values(?, ?)";
                for (Genre genre : film.getGenres()) {
                    jdbcTemplate.update(sqlQueryGenreInsert, film.getId(), genre.getId());
                }
            } else {
                String sqlQueryByGenres = "update FILM_GENRES\n" +
                        "set genre_id = ? where film_id = ?";
                for (Genre genre : film.getGenres()) {
                    update = jdbcTemplate.update(sqlQueryByGenres, genre.getId(), film.getId());
                    if (update == 0) {
                        String sqlQueryByGenresAdd = "insert into FILM_GENRES (FILM_ID, GENRE_ID)\n" +
                                "values(?, ?)";
                        jdbcTemplate.update(sqlQueryByGenresAdd, film.getId(), genre.getId());
                    }
                }
            }
        }
        return get(film.getId());
    }

    @Override
    public boolean remove(Film film) {
        String sqlQueryByGenres = "delete from FILM_GENRES\n" +
                "where film_id = ?";
        int update = jdbcTemplate.update(sqlQueryByGenres, film.getId());
        if (update == 0)
            throw new DataNotFoundException(String.format("film with id %s not single", film.getId()));

        String sqlQuery = "delete from FILMS where id = ?";
        update = jdbcTemplate.update(sqlQuery, film.getId());
        if (update != 1)
            throw new DataNotFoundException(String.format("film with id %s not single", film.getId()));
        return true;
    }

    @Override
    public Film get(Long id) {
        String sqlQuery = "select * from FILMS where id = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::createFilm, id);
        if (films.size() != 1)
            throw new DataNotFoundException(String.format("film with id %s not single", id));
        return films.get(0);
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

    int findCountGenresByFilm(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("count(genre_id)");
    }
}
