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
        parameters.put("duraion", film.getDuration());
        parameters.put("mpa_id", film.getMpa().getId());

        Number generatedId = simpleJdbcInsert.executeAndReturnKey(parameters);

        film.setId(generatedId.longValue());

        String sqlQuery = "insert into FILM_GENRES (FILM_ID, GENRE_ID)\n" +
                "values(?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
        }

        String sqlQuery1 = "SELECT g.NAME \n" +
                "FROM FILM_GENRES fg JOIN GENRES g ON fg.GENRE_ID = g.ID\n" +
                "where fg.film_id = ?";
        List<Genre> genreList = jdbcTemplate.query(sqlQuery1, GenreDbStorage::createGenre, film.getId());

        return film;
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
        return film;
    }

    @Override
    public boolean remove(Film film) {
        String sqlQuery = "delete from FILMS where id = ?";
        int update = jdbcTemplate.update(sqlQuery, film.getId());
        if (update != 1)
            throw new DataNotFoundException(String.format("film with id %s not single", film.getId()));
        return true;
    }

    @Override
    public Film get(Long id) {
        String sqlQuery = "select * from FILMS where id = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::createFilm);
        if (films.size() != 1)
            throw new DataNotFoundException(String.format("film with id %s not single", id));
        return films.get(0);
    }

     Film createFilm(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = createMpa(rs, rowNum);
        String sql = "select * from FILM_GENRES fg join genres g on fg.genre_id = g.id where id = ?";
         List<Genre> genreList = jdbcTemplate.query(sql, (result, rowNum1) ->
                 createGenre(result, rowNum1), rs.getInt("id"));
         Set<Genre> genres = new HashSet<>(genreList);
         return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .rate(rs.getInt("rate"))
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .genres(genres)
                .build();
    }
}
