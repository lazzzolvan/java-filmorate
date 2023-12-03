package ru.yandex.practicum.filmorate.storage.memory;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FriendDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    private FriendStorage friendStorage;
    private UserStorage userStorage;

    User newUser1;
    User newUser2;
    User newUser3;

    @BeforeEach
    void setUp() {
        userStorage = new UserDbStorage(jdbcTemplate);
        friendStorage = new FriendDbStorage(jdbcTemplate, userStorage);
        newUser1 = User.builder()
                .id(1L)
                .email("user1@mail.ru")
                .name("ilya Lazarev")
                .login("ilya123")
                .birthday(LocalDate.of(2003, 8, 11))
                .build();

        newUser2 = User.builder()
                .id(2L)
                .email("user2@mail.ru")
                .name("katya shirokova")
                .login("katya13")
                .birthday(LocalDate.of(2003, 11, 10))
                .build();

        newUser3 = User.builder()
                .id(3L)
                .email("user3@mail.ru")
                .name("dima sokolov")
                .login("diman")
                .birthday(LocalDate.of(2003, 04, 19))
                .build();
    }

    @Test
    void addFriendsAndGetFriendsByUser() {
        userStorage.create(newUser1);
        userStorage.create(newUser2);

        friendStorage.addFriends(newUser1.getId(), newUser2.getId());

        List<User> friendsByUser = friendStorage.getFriendsByUser(newUser1.getId());

        assertEquals(1, friendsByUser.size());
        assertEquals(newUser2, friendsByUser.get(0));
    }

    @Test
    void removeFriends() {
        userStorage.create(newUser1);
        userStorage.create(newUser2);

        friendStorage.addFriends(newUser1.getId(), newUser2.getId());

        List<User> friendsByUser = friendStorage.getFriendsByUser(newUser1.getId());

        assertEquals(1, friendsByUser.size());
        assertEquals(newUser2, friendsByUser.get(0));

        friendStorage.removeFriends(newUser1.getId(), newUser2.getId());
        List<User> friendsByUserEmpty = friendStorage.getFriendsByUser(newUser1.getId());
        assertEquals(0, friendsByUserEmpty.size());
    }


    @Test
    void getMutualFriends() {
        userStorage.create(newUser1);
        userStorage.create(newUser2);
        userStorage.create(newUser3);

        friendStorage.addFriends(newUser1.getId(), newUser2.getId());
        friendStorage.addFriends(newUser3.getId(), newUser2.getId());

        List<User> mutualFriends = friendStorage.getMutualFriends(newUser1.getId(), newUser3.getId());
        assertEquals(1, mutualFriends.size());
        assertEquals(newUser2, mutualFriends.get(0));
    }
}