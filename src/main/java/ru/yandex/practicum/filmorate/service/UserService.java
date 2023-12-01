package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public User create(User user) {
        validate(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validate(user);
        return userStorage.update(user);
    }

    public boolean remove(User user) {
        return userStorage.remove(user);
    }

    public User get(Long id) {
        return userStorage.get(id);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public boolean addFriends(Long currentUserId, Long userFriendsId) {
        userStorage.get(currentUserId);
        userStorage.get(userFriendsId);

        String sqlQuery = "insert into FRIENDS (user_id, friend_id)\n" +
                "values(?, ?)";
        jdbcTemplate.update(sqlQuery, currentUserId, userFriendsId);
        return true;
    }

    public boolean removeFriends(Long currentUserId, Long userFriendsId) {
        userStorage.get(currentUserId);
        userStorage.get(userFriendsId);

        String sqlQuery = "delete from FRIENDS where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, currentUserId, userFriendsId);
        return true;
    }

    public List<User> getFriendsByUser(Long userId) {
        String sqlQuery = "SELECT u.*\n" +
                "FROM USERS u JOIN FRIENDS f ON u.ID = f.FRIEND_ID\n" +
                "where f.user_id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, UserService::createUser, userId);
        return users;
    }

    public List<User> getMutualFriends(Long currentUserId, Long otherUserId) {
        userStorage.get(currentUserId);
        userStorage.get(otherUserId);
        String sqlQuery = "SELECT DISTINCT u.*\n" +
                "FROM USERS u\n" +
                "JOIN FRIENDS f1 ON u.id = f1.friend_id\n" +
                "JOIN FRIENDS f2 ON f1.friend_id = f2.friend_id\n" +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, UserService::createUser, currentUserId, otherUserId);
        return users;
/*        return userStorage.get(currentUserId).getFriends().stream()
                .filter(friendId -> userStorage.get(otherUserId).getFriends().contains(friendId))
                .map(userStorage::get)
                .collect(Collectors.toList());*/
    }

    public void validate(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
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
