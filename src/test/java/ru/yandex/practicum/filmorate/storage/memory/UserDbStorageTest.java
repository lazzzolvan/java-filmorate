package ru.yandex.practicum.filmorate.storage.memory;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userDbStorage;

    @BeforeEach
    void setUp() {
        userDbStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    void testFindUserById() {
        User newUser = User.builder()
                .id(1L)
                .email("user@mail.ru")
                .name("ilya Lazarev")
                .login("ilya123")
                .birthday(LocalDate.of(2003, 1, 1))
                .build();
        userDbStorage.create(newUser);

        User savedUser = userDbStorage.get(1L);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    void getAllUsers() {
        User newUser1 = User.builder()
                .id(1L)
                .email("user1@mail.ru")
                .name("ilya Lazarev")
                .login("ilya123")
                .birthday(LocalDate.of(2003, 8, 11))
                .build();

        User newUser2 = User.builder()
                .id(2L)
                .email("user2@mail.ru")
                .name("katya shirokova")
                .login("katya13")
                .birthday(LocalDate.of(2003, 11, 10))
                .build();

        userDbStorage.create(newUser1);
        userDbStorage.create(newUser2);

        List<User> users = userDbStorage.getAll();

        assertNotNull(users);
        assertEquals(2, users.size());

    }

    @Test
    void update() {
        User newUser1 = User.builder()
                .id(1L)
                .email("user1@mail.ru")
                .name("ilya Lazarev")
                .login("ilya123")
                .birthday(LocalDate.of(2003, 8, 11))
                .build();

        User newUser2 = User.builder()
                .id(1L)
                .email("user2@mail.ru")
                .name("katya shirokova")
                .login("katya13")
                .birthday(LocalDate.of(2003, 11, 10))
                .build();

        userDbStorage.create(newUser1);
        userDbStorage.update(newUser2);

        User savedUser = userDbStorage.get(1L);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isNotEqualTo(newUser1);

    }

    @Test
    void remove() {
        User newUser = User.builder()
                .id(1L)
                .email("user@mail.ru")
                .name("ilya Lazarev")
                .login("ilya123")
                .birthday(LocalDate.of(2003, 1, 1))
                .build();
        userDbStorage.create(newUser);

        userDbStorage.remove(newUser);

        List<User> users = userDbStorage.getAll();

        assertEquals(0, users.size());

    }
}