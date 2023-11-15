package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> userStorage = new HashMap<>();

    private long generateID;

    @Override
    public User create(User user) {
        user.setId(++generateID);
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public User update(User user) {
        if (!userStorage.containsKey(user.getId())) {
            throw new DataNotFoundException(String.format("User %s not found", user));
        }
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean remove(User user) {
        if (!userStorage.containsKey(user.getId())) {
            throw new DataNotFoundException(String.format("User %s not found", user));
        }
        userStorage.remove(user.getId());
        return true;
    }

    @Override
    public User get(Long id) {
        if (!userStorage.containsKey(id)) {
            throw new DataNotFoundException(String.format("User %s not found", id));
        }
        return userStorage.get(id);
    }

}
