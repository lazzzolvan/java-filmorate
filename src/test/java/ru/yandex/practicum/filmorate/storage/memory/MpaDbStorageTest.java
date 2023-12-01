package ru.yandex.practicum.filmorate.storage.memory;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class MpaDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private MpaDbStorage mpaDbStorage;

    @BeforeEach
    void setUp() {
        mpaDbStorage = new MpaDbStorage(jdbcTemplate);
    }

    @Test
    public void getAll() {
        List<Mpa> mpas = mpaDbStorage.getAll();

        assertNotNull(mpas);
    }

    @Test
    public void getById() {
        Mpa mpa = mpaDbStorage.get(3L);

        assertNotNull(mpa);
    }
}