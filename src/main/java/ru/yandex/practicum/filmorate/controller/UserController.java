package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для {@link User}
 */
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Создание пользователя.
     */
    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        log.debug("Получен запрос POST /users.");

        return userService.create(user);
    }

    /**
     * Обновление пользователя.
     */
    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        log.debug("Получен запрос PUT /users.");

        return userService.update(user);
    }

    /**
     * Добавление в друзья.
     */
    @PutMapping("/{id}/friends/{friendId}")
    public Map<String, String> addToFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        return Map.of("result", userService.addToFriends(id, friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Map<String, String> deleteFromFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        return Map.of("result",userService.deleteFromFriends(id, friendId));
    }

    /**
     * Получение списка всех пользователей.
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.get();
    }

    /**
     * Получение пользователя по идентификатору (id).
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    /**
     * Возвращаем список друзей пользователя.
     */
    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable Integer id) {
        return userService.getUserFriends(id);
    }

    /**
     * Возвращает общий список друзей двух пользователей.
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getMutualFriends(id, otherId);
    }
}
