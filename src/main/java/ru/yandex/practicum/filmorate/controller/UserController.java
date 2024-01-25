package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для {@link User}
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer userIdCounter = 1;

    /**
     * Создание пользователя
     */
    @PostMapping
    public User addUser(@RequestBody User user) {
        log.debug("Получен запрос POST /users.");
        log.debug("Попытка добавить пользователя {}.", user);

        validateUserFields(user);

        Integer id = userIdCounter++;

        log.debug("Пользователю {} назначен id = {}.", user, id);

        user.setId(id);
        User userReplaced = replaceVoidNameByLogin(user);
        users.put(id, userReplaced);

        log.debug("Пользователь {} добавлен.", userReplaced);

        return userReplaced;
    }

    /**
     * Обновление пользователя
     */
    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.debug("Получен запрос PUT /users.");
        log.debug("Попытка обновить пользователя {}.", user);

        checkUserExistsById(user);
        validateUserFields(user);

        User userReplaced = replaceVoidNameByLogin(user);
        Integer id = userReplaced.getId();

        if (users.containsKey(id)) {
            users.replace(id, userReplaced);
        }

        log.debug("Пользователь {} обновлен.", userReplaced);

        return users.get(id);
    }

    /**
     * Получение списка всех пользователей
     */
    @GetMapping
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        for (Integer key : users.keySet()) {
            userList.add(users.get(key));
        }

        return List.copyOf(userList);
    }

    private void checkUserExistsById(User user) {
        Integer id = user.getId();
        if (!users.containsKey(id)) {
            log.debug("Не найден пользователь для обновления с id = {}", id);

            throw new ValidationException(HttpStatus.NOT_FOUND, "Пользователь с id = " + id + " не найден");
        }
    }

    private void validateUserFields(User user) {
        String email = user.getEmail();
        String login = user.getLogin();
        LocalDate birthday = user.getBirthday();

        if (email == null || email.isBlank() || !email.contains("@")) {
            log.debug("Не пройдена валидация email: {}", email);

            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Параметр email не должен быть пустым и должен содержать символ @");
        }

        if (login == null || login.isBlank() || !login.replaceAll("\\s", "").equals(login)) {
            log.debug("Не пройдена валидация login: {}", login);

            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Параметр login не должен быть пустым и содержать пробелы");
        }

        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            log.debug("Не пройдена валидация birthday: {}", birthday);

            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Параметр birthday не должен быть в будущем");
        }
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
}
