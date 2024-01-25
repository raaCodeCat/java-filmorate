package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    private UserController userController;
    private User user;
    private User userToUpdate;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946,8,20))
                .build();

        userToUpdate = User.builder()
                .id(1)
                .login("doloreUpdate")
                .name("est adipisicing")
                .email("mail@yandex.ru")
                .birthday(LocalDate.of(1976,9,20))
                .build();
    }

    private void addUserToList() {
        userController.addUser(user);
    }

    @Test
    @DisplayName("Добавления пользователя, все поля валидные")
    void addUser_allFieldsValid() {
        userController.addUser(user);

        List<User> users = userController.getAllUsers();
        final User actual = users.get(0);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user, actual);
    }

    @Test
    @DisplayName("Добавления пользователя, параметр email со значением \"\"")
    void addUser_fail_blankEmail() {
        user.setEmail("");

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user)
        );

        List<User> users = userController.getAllUsers();

        assertNotNull(users);
        assertEquals(0, users.size());
        assertEquals("400 BAD_REQUEST \"Параметр email не должен быть пустым и должен содержать символ @\"", exception.getMessage());
    }

    @Test
    @DisplayName("Добавления пользователя, параметр email со значением null")
    void addUser_fail_NullEmail() {
        user.setEmail(null);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user)
        );

        List<User> users = userController.getAllUsers();

        assertNotNull(users);
        assertEquals(0, users.size());
        assertEquals("400 BAD_REQUEST \"Параметр email не должен быть пустым и должен содержать символ @\"", exception.getMessage());
    }

    @Test
    @DisplayName("Добавления пользователя, параметр email без @")
    void addUser_fail_emailWithoutImportantSymbol() {
        user.setEmail("mAtd.ru");

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user)
        );

        List<User> users = userController.getAllUsers();

        assertNotNull(users);
        assertEquals(0, users.size());
        assertEquals("400 BAD_REQUEST \"Параметр email не должен быть пустым и должен содержать символ @\"", exception.getMessage());
    }

    @Test
    @DisplayName("Добавления пользователя, параметр login со значением \"\"")
    void addUser_fail_blankLogin() {
        user.setLogin("");

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user)
        );

        List<User> users = userController.getAllUsers();

        assertNotNull(users);
        assertEquals(0, users.size());
        assertEquals("400 BAD_REQUEST \"Параметр login не должен быть пустым и содержать пробелы\"", exception.getMessage());
    }

    @Test
    @DisplayName("Добавления пользователя, параметр login со значением null")
    void addUser_fail_NullLogin() {
        user.setLogin("");

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user)
        );

        List<User> users = userController.getAllUsers();

        assertNotNull(users);
        assertEquals(0, users.size());
        assertEquals("400 BAD_REQUEST \"Параметр login не должен быть пустым и содержать пробелы\"", exception.getMessage());
    }

    @Test
    @DisplayName("Добавления пользователя, параметр login с пробелами")
    void addUser_fail_loginWithWhitespace() {
        user.setLogin("Login with whitespace");

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user)
        );

        List<User> users = userController.getAllUsers();

        assertNotNull(users);
        assertEquals(0, users.size());
        assertEquals("400 BAD_REQUEST \"Параметр login не должен быть пустым и содержать пробелы\"", exception.getMessage());
    }

    @Test
    @DisplayName("Добавления пользователя, дата рождения (birthday) в \"будущем\"")
    void addUser_fail_birthdayInFuture() {
        user.setBirthday(LocalDate.of(3000, 1, 1));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user)
        );

        List<User> users = userController.getAllUsers();

        assertNotNull(users);
        assertEquals(0, users.size());
        assertEquals("400 BAD_REQUEST \"Параметр birthday не должен быть в будущем\"", exception.getMessage());
    }

    @Test
    @DisplayName("Добавления пользователя, параметр name со значениями \"\"")
    void addUser_fail_BlankName() {
        user.setName("");
        userController.addUser(user);
        user.setName(user.getLogin());

        List<User> users = userController.getAllUsers();
        final User actual = users.get(0);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user, actual);
    }

    @Test
    @DisplayName("Добавления пользователя, параметр name со значениями null")
    void addUser_fail_NullName() {
        user.setName(null);
        userController.addUser(user);
        user.setName(user.getLogin());

        List<User> users = userController.getAllUsers();
        final User actual = users.get(0);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user, actual);
    }

    @Test
    @DisplayName("Добавления пользователя, дата рождения (birthday) не в \"будущем\"")
    void addUser_fail_birthdayNotInFuture() {
        user.setBirthday(LocalDate.now());
        userController.addUser(user);

        List<User> users = userController.getAllUsers();
        final User actual = users.get(0);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user, actual);
    }

    @Test
    @DisplayName("Обновление пользователя, все поля валидные")
    void updateFilm_allFieldsValid() {
        addUserToList();
        userController.updateUser(userToUpdate);

        List<User> users = userController.getAllUsers();
        final User actual = users.get(0);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(userToUpdate, actual);
    }

    @Test
    @DisplayName("Обновление пользователя, не правильный id")
    void updateFilm_fail_invalidId() {
        addUserToList();
        userToUpdate.setId(777);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.updateUser(userToUpdate)
        );

        assertEquals("404 NOT_FOUND \"Пользователь с id = 777 не найден\"",
                exception.getMessage());
    }
}