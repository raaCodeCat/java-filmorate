package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
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
    public User addUser(@RequestBody @Valid User user) {
        log.debug("Получен запрос POST /users.");
        log.debug("Попытка добавить пользователя {}.", user);

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
    public User updateUser(@RequestBody @Valid User user) {
        log.debug("Получен запрос PUT /users.");
        log.debug("Попытка обновить пользователя {}.", user);

        checkUserExistsById(user);

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }
}
