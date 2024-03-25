package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для {@link User}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    private final FriendshipStorage friendshipStorage;

    @Override
    public List<User> get() {
        return userStorage.get();
    }

    @Override
    public User getById(Integer id) {
        Optional<User> userOptional = userStorage.getById(id);

        if (userOptional.isEmpty()) {
            throw new ValidationException(HttpStatus.NOT_FOUND, "Пользователь с id = " + id + " не найден");
        }

        return userOptional.get();
    }

    @Override
    public User create(User user) {
        log.debug("Попытка добавить пользователя {}.", user);

        User userReplaced = replaceVoidNameByLogin(user);
        Integer userId = userStorage.create(userReplaced);

        log.debug("Добавляем пользователя {}.", userReplaced);

        return userStorage.getById(userId).orElse(null);
    }

    @Override
    public User update(User user) {
        log.debug("Попытка обновить пользователя {}.", user);

        Integer userId = user.getId();
        checkExistsUserById(userId);

        User userReplaced = replaceVoidNameByLogin(user);

        log.debug("Обновляем пользователя {}.", userReplaced);
        userStorage.update(userId, userReplaced);

        return userStorage.getById(userId).orElse(null);
    }

    @Override
    public String addToFriends(Integer id, Integer friendId) {
        checkExistsUserById(id);
        checkExistsUserById(friendId);

        friendshipStorage.createFriendship(id, friendId);

        return "Пользователь с id " + id + " добавил в друзья пользователя с id " + friendId + ".";
    }

    @Override
    public String deleteFromFriends(Integer id, Integer friendId) {
        checkExistsUserById(id);
        checkExistsUserById(friendId);

        friendshipStorage.deleteFriendship(id, friendId);

        return "Пользователь с id " + id + " удалил из друзей пользователя с id " + friendId + ".";
    }

    @Override
    public List<User> getUserFriends(Integer id) {
        checkExistsUserById(id);

        return friendshipStorage.getFriendsByUserId(id);
    }

    @Override
    public List<User> getMutualFriends(Integer id, Integer otherId) {
        checkExistsUserById(id);
        checkExistsUserById(otherId);

        return friendshipStorage.getMutualFriends(id, otherId);
    }

    private User replaceVoidNameByLogin(User user) {
        String name = user.getName();
        String newName;

        if (name == null || name.isBlank()) {
            newName = user.getLogin();

            log.debug("У пользователя отсутствует имя, заменено логином");
        } else {
            newName = user.getName();
        }

        return User.builder()
                .name(newName)
                .login(user.getLogin())
                .email(user.getEmail())
                .birthday(user.getBirthday())
                .id(user.getId())
                .build();
    }

    private void checkExistsUserById(Integer id) {
        Optional<User> userOptional = userStorage.getById(id);

        if (userOptional.isEmpty()) {
            log.debug("Не найден пользователь для обновления с id = {}", id);

            throw new ValidationException(HttpStatus.NOT_FOUND, "Пользователь с id = " + id + " не найден");
        }
    }
}
