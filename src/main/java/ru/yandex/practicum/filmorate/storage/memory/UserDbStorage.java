package ru.yandex.practicum.filmorate.storage.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("USERS")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("login", user.getLogin());
        parameters.put("name", user.getName());
        parameters.put("email", user.getEmail());
        parameters.put("birthday", user.getBirthday());

        Number generatedId = simpleJdbcInsert.executeAndReturnKey(parameters);

        user.setId(generatedId.longValue());
        return user;
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "select * from USERS";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::createUser);
    }

    @Override
    public User update(User user) {
        String sqlQuery = "update USERS\n" +
                "set login = ?, name = ?, email = ?, birthday = ?\n" +
                "where id = ?";
        int update = jdbcTemplate.update(sqlQuery, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
        if (update != 1)
            throw new DataNotFoundException(String.format("user with id %s not single", user.getId()));
        return user;
    }

    @Override
    public boolean remove(User user) {
        String sqlQuery = "delete from USERS where id = ?";
        int update = jdbcTemplate.update(sqlQuery, user.getId());
        if (update != 1)
            throw new DataNotFoundException(String.format("user with id %s not single", user.getId()));
        return true;
    }

    @Override
    public User get(Long id) {
        String sqlQuery = "select * from USERS where id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, UserDbStorage::createUser, id);
        if (users.size() != 1)
            throw new DataNotFoundException(String.format("user with id %s not single", id));
        return users.get(0);
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
