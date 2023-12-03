package ru.yandex.practicum.filmorate.storage.memory;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class LikeDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;
    private FilmStorage filmStorage;
    private MpaDbStorage mpaDbStorage;
    private LikeDbStorage likeStorage;
    Film film;
    User newUser1;

    @BeforeEach
    void setUp() {
        userStorage = new UserDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate);
        mpaDbStorage = new MpaDbStorage(jdbcTemplate);
        likeStorage = new LikeDbStorage(jdbcTemplate, userStorage, filmStorage);

        film = Film.builder()
                .id(1L)
                .name("Java")
                .description("I love Java")
                .rate(4)
                .duration(119)
                .releaseDate(LocalDate.of(2023, 3, 4))
                .mpa(mpaDbStorage.get(1L))
                .build();
        newUser1 = User.builder()
                .id(1L)
                .email("user1@mail.ru")
                .name("ilya Lazarev")
                .login("ilya123")
                .birthday(LocalDate.of(2003, 8, 11))
                .build();
    }

    @Test
    void addLikeFilmAndGetTopFilms() {
        filmStorage.create(film);
        userStorage.create(newUser1);

        likeStorage.addLikeFilm(film.getId(), newUser1.getId());
        List<Film> firstCountFilms = likeStorage.getFirstCountFilms(1L);

        assertEquals(1, firstCountFilms.size());
        assertEquals(film, firstCountFilms.get(0));
    }
}