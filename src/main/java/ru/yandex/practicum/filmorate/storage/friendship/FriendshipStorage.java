package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseGetStorage;

import java.util.List;

public interface FriendshipStorage extends BaseGetStorage<Friendship> {
    List<User> getFriendsByUserId(Integer id);

    Integer createFriendship(Integer userId, Integer friendId);

    void deleteFriendship(Integer userId, Integer friendId);

    List<User> getMutualFriends(Integer userId, Integer otherUserId);
}
