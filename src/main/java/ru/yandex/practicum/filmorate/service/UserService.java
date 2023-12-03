package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    private final FriendStorage friendStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
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
        return friendStorage.addFriends(currentUserId, userFriendsId);
    }

    public boolean removeFriends(Long currentUserId, Long userFriendsId) {
        return friendStorage.removeFriends(currentUserId, userFriendsId);
    }

    public List<User> getFriendsByUser(Long userId) {
        return friendStorage.getFriendsByUser(userId);
    }

    public List<User> getMutualFriends(Long currentUserId, Long otherUserId) {
        return friendStorage.getMutualFriends(currentUserId, otherUserId);
    }

    public void validate(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
