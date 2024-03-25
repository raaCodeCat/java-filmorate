package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendshipDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FriendshipDbStorage friendshipDbStorage;
    private UserDbStorage userDbStorage;
    private User newUser1;
    private User newUser2;

    @BeforeEach
    void setUp() {
        friendshipDbStorage = new FriendshipDbStorage(jdbcTemplate);
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
        Integer id = friendshipDbStorage.createFriendship(userId1, userId2);

        List<Friendship> friendshipList = friendshipDbStorage.get();

        assertNotNull(friendshipList);
        assertEquals(1, friendshipList.size());
        assertEquals(id, friendshipList.get(0).getId());
        assertEquals(userId1, friendshipList.get(0).getUser().getId());
        assertEquals(userId2, friendshipList.get(0).getUserFriend().getId());
    }

    @Test
    void getFriendsByUserId() {
        Integer userId1 = userDbStorage.create(newUser1);
        Integer userId2 = userDbStorage.create(newUser2);
        friendshipDbStorage.createFriendship(userId1, userId2);

        List<User> friends = friendshipDbStorage.getFriendsByUserId(userId1);

        assertNotNull(friends);
        assertEquals(1, friends.size());
        assertEquals(userId2, friends.get(0).getId());
    }

    @Test
    void getById() {
        Integer userId1 = userDbStorage.create(newUser1);
        Integer userId2 = userDbStorage.create(newUser2);
        Integer id = friendshipDbStorage.createFriendship(userId1, userId2);

        Optional<Friendship> getedFriendship = friendshipDbStorage.getById(id);

        assertTrue(getedFriendship.isPresent());
        assertEquals(id, getedFriendship.get().getId());
        assertEquals(userId1, getedFriendship.get().getUser().getId());
        assertEquals(userId2, getedFriendship.get().getUserFriend().getId());
    }

    @Test
    void createFriendshipOneSide() {
        Integer userId1 = userDbStorage.create(newUser1);
        Integer userId2 = userDbStorage.create(newUser2);

        friendshipDbStorage.createFriendship(userId1, userId2);
        List<Friendship> friendshipList = friendshipDbStorage.get();

        assertNotNull(friendshipList);
        assertEquals(1, friendshipList.size());
        assertEquals(userId1, friendshipList.get(0).getUser().getId());
        assertEquals(userId2, friendshipList.get(0).getUserFriend().getId());
        assertFalse(friendshipList.get(0).getIsConfirmed());
    }

    @Test
    void createFriendshipTowSide() {
        Integer userId1 = userDbStorage.create(newUser1);
        Integer userId2 = userDbStorage.create(newUser2);

        friendshipDbStorage.createFriendship(userId1, userId2);
        friendshipDbStorage.createFriendship(userId2, userId1);
        List<Friendship> friendshipList = friendshipDbStorage.get();

        assertNotNull(friendshipList);
        assertEquals(2, friendshipList.size());
        assertEquals(userId1, friendshipList.get(0).getUser().getId());
        assertEquals(userId2, friendshipList.get(0).getUserFriend().getId());
        assertTrue(friendshipList.get(0).getIsConfirmed());
        assertEquals(userId2, friendshipList.get(1).getUser().getId());
        assertEquals(userId1, friendshipList.get(1).getUserFriend().getId());
        assertTrue(friendshipList.get(1).getIsConfirmed());
    }

    @Test
    void deleteFriendshipOneSide() {
        Integer userId1 = userDbStorage.create(newUser1);
        Integer userId2 = userDbStorage.create(newUser2);

        friendshipDbStorage.createFriendship(userId1, userId2);
        friendshipDbStorage.deleteFriendship(userId1, userId2);
        List<Friendship> friendshipList = friendshipDbStorage.get();

        assertNotNull(friendshipList);
        assertEquals(0, friendshipList.size());
    }

    @Test
    void deleteFriendshipTwoSide() {
        Integer userId1 = userDbStorage.create(newUser1);
        Integer userId2 = userDbStorage.create(newUser2);

        friendshipDbStorage.createFriendship(userId1, userId2);
        friendshipDbStorage.createFriendship(userId2, userId1);
        friendshipDbStorage.deleteFriendship(userId1, userId2);
        List<Friendship> friendshipList = friendshipDbStorage.get();

        assertNotNull(friendshipList);
        assertEquals(1, friendshipList.size());
        assertEquals(userId2, friendshipList.get(0).getUser().getId());
        assertEquals(userId1, friendshipList.get(0).getUserFriend().getId());
        assertFalse(friendshipList.get(0).getIsConfirmed());
    }

    @Test
    void getMutualFriends() {
        Integer userId1 = userDbStorage.create(newUser1);
        Integer userId2 = userDbStorage.create(newUser2);
        User newUser3 = User.builder()
                .login("third")
                .name("thirdName")
                .email("third@mail.ru")
                .birthday(LocalDate.of(2003, 3, 13))
                .build();
        Integer userId3 = userDbStorage.create(newUser3);
        friendshipDbStorage.createFriendship(userId1, userId2);
        friendshipDbStorage.createFriendship(userId1, userId3);
        friendshipDbStorage.createFriendship(userId2, userId3);

        List<User> users = friendshipDbStorage.getMutualFriends(userId1, userId2);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(userId3, users.get(0).getId());
    }
}