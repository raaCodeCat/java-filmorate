package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import java.util.List;

/**
 * Хранилище для {@link User}.
 */
public interface UserStorage extends BaseStorage<User> {
    void addToFriends(Integer id, Integer friendId);

    void deleteFromFriends(Integer id, Integer friendId);

    List<User> getUserFriends(Integer id);

    List<User> getMutualFriends(Integer id, Integer otherId);
}
