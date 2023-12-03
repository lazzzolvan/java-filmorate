package ru.yandex.practicum.filmorate.storage.memory;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class GenreDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private GenreDbStorage genreDbStorage;

    @BeforeEach
    void setUp() {
        genreDbStorage = new GenreDbStorage(jdbcTemplate);
    }

    @Test
    public void getAll() {
        List<Genre> genres = genreDbStorage.getAll();

        assertNotNull(genres);
    }

    @Test
    public void getById() {
        Genre genre = genreDbStorage.get(2L);

        assertNotNull(genre);
    }

}