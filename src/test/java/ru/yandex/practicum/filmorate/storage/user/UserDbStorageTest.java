package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userDbStorage;
    private User newUser1;
    private User newUser2;

    @BeforeEach
    void setUp() {
        userDbStorage = new UserDbStorage(jdbcTemplate);
        newUser1 = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        newUser2 = User.builder()
                .login("catman")
                .name("mrCat")
                .email("cat@mail.ru")
                .birthday(LocalDate.of(2004, 11, 24))
                .build();
    }

    @Test
    void get() {
        Integer userId1 = userDbStorage.create(newUser1);
        Integer userId2 = userDbStorage.create(newUser2);
        Optional<User> user1 = userDbStorage.getById(userId1);
        Optional<User> user2 = userDbStorage.getById(userId2);

        List<User> users = userDbStorage.get();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(user1.isPresent());
        assertEquals(user1.get(), users.get(0));
        assertTrue(user2.isPresent());
        assertEquals(user2.get(), users.get(1));
    }

    @Test
    void getById() {
        Integer userId1 = userDbStorage.create(newUser1);

        Optional<User> user = userDbStorage.getById(userId1);

        assertTrue(user.isPresent());
        assertEquals(userId1, user.get().getId());
        assertEquals(newUser1.getLogin(), user.get().getLogin());
        assertEquals(newUser1.getName(), user.get().getName());
        assertEquals(newUser1.getBirthday(), user.get().getBirthday());
        assertEquals(newUser1.getEmail(), user.get().getEmail());
    }

    @Test
    void create() {
        Integer userId1 = userDbStorage.create(newUser1);

        Optional<User> user = userDbStorage.getById(userId1);

        assertTrue(user.isPresent());
        assertEquals(userId1, user.get().getId());
        assertEquals(newUser1.getLogin(), user.get().getLogin());
        assertEquals(newUser1.getName(), user.get().getName());
        assertEquals(newUser1.getBirthday(), user.get().getBirthday());
        assertEquals(newUser1.getEmail(), user.get().getEmail());
    }

    @Test
    void update() {
        Integer userId1 = userDbStorage.create(newUser1);
        newUser1.setId(userId1);
        newUser1.setName("udpateName");
        newUser1.setLogin("updateLogin");
        newUser1.setEmail("update@mail.ru");

        userDbStorage.update(userId1, newUser1);
        Optional<User> user = userDbStorage.getById(userId1);

        assertTrue(user.isPresent());
        assertEquals(newUser1, user.get());
    }
}