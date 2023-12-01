package ru.yandex.practicum.filmorate.storage.memory;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    void testFindUserById() {
        User newUser = User.builder()
                .id(1l)
                .email("user@mail.ru")
                .name("ilya Lazarev")
                .login("ilya123")
                .birthday(LocalDate.of(2003,1,1))
                .build();
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userDbStorage.create(newUser);

        User savedUser = userDbStorage.get(1l);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }
}