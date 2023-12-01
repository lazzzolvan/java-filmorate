package ru.yandex.practicum.filmorate.storage.memory;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    private FilmDbStorage filmDbStorage;

    private MpaDbStorage mpaDbStorage;

    private GenreDbStorage genreDbStorage;

    @BeforeEach
    void setUp() {
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
        mpaDbStorage = new MpaDbStorage(jdbcTemplate);
        genreDbStorage = new GenreDbStorage(jdbcTemplate);
    }

    @Test
    void findFilmById() {
        Film film = Film.builder()
                .id(1L)
                .name("Java")
                .description("I love Java")
                .rate(4)
                .duration(119)
                .releaseDate(LocalDate.of(2023, 3, 4))
                .mpa(mpaDbStorage.get(1L))
                .build();

        filmDbStorage.create(film);

        Film savedFilm = filmDbStorage.get(1L);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    void getAllFilms() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Java")
                .description("I love Java")
                .rate(4)
                .duration(119)
                .releaseDate(LocalDate.of(2023, 3, 4))
                .mpa(mpaDbStorage.get(1L))
                .build();

        Film film2 = Film.builder()
                .id(2L)
                .name("C#")
                .description("I love c#")
                .rate(1)
                .duration(109)
                .releaseDate(LocalDate.of(2021, 3, 4))
                .mpa(mpaDbStorage.get(2L))
                .build();

        filmDbStorage.create(film1);
        filmDbStorage.create(film2);

        List<Film> films = filmDbStorage.getAll();

        assertNotNull(films);
        assertEquals(2, films.size());
    }

    @Test
    void update() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Java")
                .description("I love Java")
                .rate(4)
                .duration(119)
                .releaseDate(LocalDate.of(2023, 3, 4))
                .mpa(mpaDbStorage.get(1L))
                .build();

        Film film2 = Film.builder()
                .id(1L)
                .name("C#")
                .description("I love c#")
                .rate(1)
                .duration(109)
                .releaseDate(LocalDate.of(2021, 3, 4))
                .mpa(mpaDbStorage.get(2L))
                .build();

        filmDbStorage.create(film1);
        filmDbStorage.update(film2);

        Film film = filmDbStorage.get(1L);

        assertThat(film)
                .isNotNull()
                .usingRecursiveComparison()
                .isNotEqualTo(film1);
    }

    @Test
    void remove() {
        Set<Genre> genres = new HashSet<>();
        genres.add(genreDbStorage.get(1L));
        Film film = Film.builder()
                .id(1L)
                .name("Java")
                .description("I love Java")
                .rate(4)
                .duration(119)
                .releaseDate(LocalDate.of(2023, 3, 4))
                .mpa(mpaDbStorage.get(1L))
                .genres(genres)
                .build();

        filmDbStorage.create(film);

        filmDbStorage.remove(film);

        List<Film> films = filmDbStorage.getAll();

        assertEquals(0, films.size());
    }
}