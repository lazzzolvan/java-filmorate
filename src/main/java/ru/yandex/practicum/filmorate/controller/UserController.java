package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        userStorage = inMemoryUserStorage;
        this.userService = userService;
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

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        log.info("Get by id user {}", id);
        return userStorage.get(id);
    }

    @DeleteMapping
    public boolean remove(User user) {
        log.info("Delete user {}", user);
        return userStorage.remove(user);
    }

    @PutMapping("/{id}/friends/{friendsId}")
    public boolean addFriends(@PathVariable Long id, @PathVariable Long friendsId) {
        log.info("Add friends user {}, friendsUser {}", userStorage.get(id), userStorage.get(friendsId));
        return userService.addFriends(id, friendsId);
    }

    @DeleteMapping("/{id}/friends/{friendsId}")
    public boolean removeFriends(@PathVariable Long id, @PathVariable Long friendsId) {
        log.info("Remove friends user {}", userStorage.get(id));
        return userService.removeFriends(id, friendsId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsByUser(@PathVariable Long id) {
        log.info("Get friends by user {}", userStorage.get(id));
        return userService.getFriendsByUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Get mutual friends by user {}", userStorage.get(id));
        return userService.getMutualFriends(id, otherId);
    }
}
