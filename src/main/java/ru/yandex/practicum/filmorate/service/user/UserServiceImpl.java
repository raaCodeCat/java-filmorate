package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
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
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

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
        User userNew = userStorage.create(userReplaced);
        log.debug("Пользователь {} добавлен.", userNew);

        return userNew;
    }

    @Override
    public User update(User user) {
        log.debug("Попытка обновить пользователя {}.", user);

        Integer id = user.getId();
        checkExistsUserById(id);

        User userReplaced = replaceVoidNameByLogin(user);
        User userUpdated = userStorage.update(id, userReplaced);
        log.debug("Пользователь {} обновлен.", userReplaced);

        return userUpdated;
    }

    @Override
    public String addToFriends(Integer id, Integer friendId) {
        checkExistsUserById(id);
        checkExistsUserById(friendId);

        userStorage.addToFriends(id, friendId);

        return "Пользователь с id " + id + " добавил в друзья пользователя с id " + friendId + ".";
    }

    @Override
    public String deleteFromFriends(Integer id, Integer friendId) {
        checkExistsUserById(id);
        checkExistsUserById(friendId);

        userStorage.deleteFromFriends(id, friendId);

        return "Пользователь с id " + id + " удалил из друзей пользователя с id " + friendId + ".";
    }

    @Override
    public List<User> getUserFriends(Integer id) {
        checkExistsUserById(id);

        return userStorage.getUserFriends(id);
    }

    @Override
    public List<User> getMutualFriends(Integer id, Integer otherId) {
        checkExistsUserById(id);
        checkExistsUserById(otherId);

        return userStorage.getMutualFriends(id, otherId);
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
