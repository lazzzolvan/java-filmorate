package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    public FriendDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public boolean addFriends(Long currentUserId, Long userFriendsId) {
        userStorage.get(currentUserId);
        userStorage.get(userFriendsId);

        String sqlQuery = "insert into FRIENDS (user_id, friend_id)\n" +
                "values(?, ?)";
        jdbcTemplate.update(sqlQuery, currentUserId, userFriendsId);
        return true;
    }

    @Override
    public boolean removeFriends(Long currentUserId, Long userFriendsId) {
        userStorage.get(currentUserId);
        userStorage.get(userFriendsId);

        String sqlQuery = "delete from FRIENDS where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, currentUserId, userFriendsId);
        return true;
    }

    @Override
    public List<User> getFriendsByUser(Long userId) {
        String sqlQuery = "SELECT u.*\n" +
                "FROM USERS u JOIN FRIENDS f ON u.ID = f.FRIEND_ID\n" +
                "where f.user_id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, FriendDbStorage::createUser, userId);
        return users;
    }

    @Override
    public List<User> getMutualFriends(Long currentUserId, Long otherUserId) {
        userStorage.get(currentUserId);
        userStorage.get(otherUserId);
        String sqlQuery = "SELECT DISTINCT u.*\n" +
                "FROM USERS u\n" +
                "JOIN FRIENDS f1 ON u.id = f1.friend_id\n" +
                "JOIN FRIENDS f2 ON f1.friend_id = f2.friend_id\n" +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, FriendDbStorage::createUser, currentUserId, otherUserId);
        return users;
    }

    static User createUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
