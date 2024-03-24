package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.BaseService;

import java.util.List;

/**
 * Интерфейс сервиса для {@link User}.
 */
public interface UserService extends BaseService<User> {
    String addToFriends(Integer id, Integer friendId);

    String deleteFromFriends(Integer id, Integer friendId);

    List<User> getUserFriends(Integer id);

    List<User> getMutualFriends(Integer id, Integer otherId);
}
