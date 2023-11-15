package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
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
        userStorage.get(currentUserId).addFriends(userFriendsId);
        userStorage.get(userFriendsId).addFriends(currentUserId);
        return true;
    }

    public boolean removeFriends(Long currentUserId, Long userFriendsId) {
        userStorage.get(currentUserId).getFriends().remove(userFriendsId);
        userStorage.get(userFriendsId).getFriends().remove(currentUserId);
        return true;
    }

    public List<User> getFriendsByUser(Long userId) {
        return userStorage.get(userId).getFriends().stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Long currentUserId, Long otherUserId) {
        return userStorage.get(currentUserId).getFriends().stream()
                .filter(friendId -> userStorage.get(otherUserId).getFriends().contains(friendId))
                .map(userStorage::get)
                .collect(Collectors.toList());
    }

    public void validate(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }


}
