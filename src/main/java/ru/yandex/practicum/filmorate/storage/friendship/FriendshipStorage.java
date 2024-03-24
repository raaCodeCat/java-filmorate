package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

public interface FriendshipStorage extends BaseStorage<Friendship> {
    List<User> getFriendsByUserId(Integer id);

    void createFriendship(Integer userId, Integer friendId);

    void deleteFriendship(Integer userId, Integer friendId);

    List<User> getMutualFriends(Integer userId, Integer otherUserId);
}
