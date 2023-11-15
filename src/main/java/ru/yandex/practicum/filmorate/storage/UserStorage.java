package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    List<User> getAll();

    User update(User user);

    boolean remove(User user);

    User get(Long id);
}
