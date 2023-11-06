package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage) {
        userStorage = inMemoryUserStorage;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Creating user {}", user);
        return userStorage.create(user);
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Get all users");
        return userStorage.getAll();
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Updating user {}", user);
        return userStorage.update(user);
    }

    @GetMapping
    public User get(@RequestBody Long id) {
        log.info("Get by id user {}", id);
        return userStorage.get(id);
    }

    @DeleteMapping
    public boolean remove(User user) {
        log.info("Delete user {}", user);
        return userStorage.remove(user);
    }

}
