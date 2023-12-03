package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    boolean addFriends(Long currentUserId, Long userFriendsId);

    boolean removeFriends(Long currentUserId, Long userFriendsId);

    List<User> getFriendsByUser(Long userId);

    List<User> getMutualFriends(Long currentUserId, Long otherUserId);
}
