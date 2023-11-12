package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public boolean addFriends(Long currentUserId, Long userFriendsId) {
        if (userStorage.get(currentUserId) != null && userStorage.get(userFriendsId) != null) {
            userStorage.get(currentUserId).addFriends(userFriendsId);
            userStorage.get(userFriendsId).addFriends(currentUserId);
            return true;
        } else {
            throw new DataNotFoundException("Не найден данный пользователь или пользователь к которому вы " +
                    "хотите добавиться в друзья");
        }
    }

    public boolean removeFriends(Long currentUserId, Long userFriendsId) {
        if (userStorage.get(currentUserId) != null && userStorage.get(userFriendsId) != null) {
            userStorage.get(currentUserId).getFriends().remove(userFriendsId);
            userStorage.get(userFriendsId).getFriends().remove(currentUserId);
            return true;
        } else {
            throw new DataNotFoundException("Не найден данный пользователь или пользователь которого вы " +
                    "хотите удалить из друзей");
        }
    }

    public List<User> getFriendsByUser(Long userId) {
        if (userStorage.get(userId) != null) {
            List<User> friendsByUser = new ArrayList<>();
            for (Long friend : userStorage.get(userId).getFriends()) {
                friendsByUser.add(userStorage.get(friend));
            }
            return friendsByUser;
        } else {
            throw new DataNotFoundException("Данный пользователь не найден");
        }
    }

    public List<User> getMutualFriends(Long currentUserId, Long otherUserId) {
        if (userStorage.get(currentUserId) != null && userStorage.get(otherUserId) != null) {
            List<User> mutualFriends = new ArrayList<>();
            if (userStorage.get(currentUserId).getFriends() == null || userStorage.get(otherUserId).getFriends() == null) {
                return mutualFriends;
            }
            for (Long friend : userStorage.get(currentUserId).getFriends()) {
                if (userStorage.get(otherUserId).getFriends().contains(friend)) {
                    mutualFriends.add(userStorage.get(friend));
                }
            }
            return mutualFriends;
        } else {
            throw new DataNotFoundException("Данные пользователи не найдены");
        }
    }


}
