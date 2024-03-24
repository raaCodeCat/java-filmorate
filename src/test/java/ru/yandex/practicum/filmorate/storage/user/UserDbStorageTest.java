package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDbStorage;
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
        userDbStorage = new UserDbStorage(
                jdbcTemplate,
                new FriendshipDbStorage(jdbcTemplate)
        );
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
        User user1 = userDbStorage.create(newUser1);
        Integer userId1 = user1.getId();
        user1.setId(userId1);
        User user2 = userDbStorage.create(newUser2);
        Integer userId2 = user2.getId();
        user2.setId(userId2);

        List<User> users = userDbStorage.get();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(user1, users.get(0));
        assertEquals(user2, users.get(1));
    }

    @Test
    void getById() {
        User user1 = userDbStorage.create(newUser1);
        Integer userId1 = user1.getId();
        user1.setId(userId1);

        Optional<User> user = userDbStorage.getById(userId1);

        assertTrue(user.isPresent());
        assertEquals(user1, user.get());
    }

    @Test
    void create() {
        User user1 = userDbStorage.create(newUser1);
        Integer userId1 = user1.getId();
        user1.setId(userId1);

        Optional<User> user = userDbStorage.getById(userId1);

        assertTrue(user.isPresent());
        assertEquals(user1, user.get());
    }

    @Test
    void update() {
        User user1 = userDbStorage.create(newUser1);
        Integer userId1 = user1.getId();
        user1.setId(userId1);
        user1.setName("udpateName");
        user1.setLogin("updateLogin");
        user1.setEmail("update@mail.ru");

        userDbStorage.update(userId1, user1);
        Optional<User> user = userDbStorage.getById(userId1);

        assertTrue(user.isPresent());
        assertEquals(user1, user.get());
    }

    @Test
    void getUserFriends() {
        FriendshipDbStorage friendshipDbStorage = new FriendshipDbStorage(jdbcTemplate);
        User user1 = userDbStorage.create(newUser1);
        Integer userId1 = user1.getId();
        user1.setId(userId1);
        User user2 = userDbStorage.create(newUser2);
        Integer userId2 = user2.getId();
        user2.setId(userId2);
        friendshipDbStorage.createFriendship(userId1, userId2);

        List<User> users = userDbStorage.getUserFriends(userId1);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user2, users.get(0));
    }

    @Test
    void getMutualFriends() {
        FriendshipDbStorage friendshipDbStorage = new FriendshipDbStorage(jdbcTemplate);
        User user1 = userDbStorage.create(newUser1);
        Integer userId1 = user1.getId();
        user1.setId(userId1);
        User user2 = userDbStorage.create(newUser2);
        Integer userId2 = user2.getId();
        user2.setId(userId2);
        User newUser3 = User.builder()
                .login("third")
                .name("thirdName")
                .email("third@mail.ru")
                .birthday(LocalDate.of(2003, 3, 13))
                .build();
        User user3 = userDbStorage.create(newUser3);
        Integer userId3 = user3.getId();
        user3.setId(userId3);
        friendshipDbStorage.createFriendship(userId1, userId2);
        friendshipDbStorage.createFriendship(userId1, userId3);
        friendshipDbStorage.createFriendship(userId2, userId3);

        List<User> users = friendshipDbStorage.getMutualFriends(userId1, userId2);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user3, users.get(0));
    }
}