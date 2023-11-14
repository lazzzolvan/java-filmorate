package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Creating user {}", user);
        return userService.create(user);
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Get all users");
        return userService.getAll();
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Updating user {}", user);
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        log.info("Get by id user {}", id);
        return userService.get(id);
    }

    @DeleteMapping
    public boolean remove(User user) {
        log.info("Delete user {}", user);
        return userService.remove(user);
    }

    @PutMapping("/{id}/friends/{friendsId}")
    public boolean addFriends(@PathVariable Long id, @PathVariable Long friendsId) {
        log.info("Add friends user {}, friendsUser {}", userService.get(id), userService.get(friendsId));
        return userService.addFriends(id, friendsId);
    }

    @DeleteMapping("/{id}/friends/{friendsId}")
    public boolean removeFriends(@PathVariable Long id, @PathVariable Long friendsId) {
        log.info("Remove friends user {}", userService.get(id));
        return userService.removeFriends(id, friendsId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsByUser(@PathVariable Long id) {
        log.info("Get friends by user {}", userService.get(id));
        return userService.getFriendsByUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Get mutual friends by user {}", userService.get(id));
        return userService.getMutualFriends(id, otherId);
    }
}
