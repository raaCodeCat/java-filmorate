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
        Friendship newFriendship = Friendship.builder()
                .user(user1)
                .userFriend(user2)
                .isConfirmed(false)
                .build();
        Friendship friendship = friendshipDbStorage.create(newFriendship);
        Integer id = friendship.getId();

        List<Friendship> friendshipList = friendshipDbStorage.get();

        assertNotNull(friendshipList);
        assertEquals(1, friendshipList.size());
        assertEquals(id, friendshipList.get(0).getId());
        assertEquals(userId1, friendshipList.get(0).getUser().getId());
        assertEquals(userId2, friendshipList.get(0).getUserFriend().getId());
    }

    @Test
    void getFriendsByUserId() {
        User user1 = userDbStorage.create(newUser1);
        Integer userId1 = user1.getId();
        User user2 = userDbStorage.create(newUser2);
        Integer userId2 = user2.getId();
        user2.setId(userId2);

        Friendship newFriendship = Friendship.builder()
                .user(user1)
                .userFriend(user2)
                .isConfirmed(false)
                .build();
        friendshipDbStorage.create(newFriendship);

        List<User> friends = friendshipDbStorage.getFriendsByUserId(userId1);

        assertNotNull(friends);
        assertEquals(1, friends.size());
        assertEquals(user2, friends.get(0));
    }

    @Test
    void getById() {
        User user1 = userDbStorage.create(newUser1);
        Integer userId1 = user1.getId();
        User user2 = userDbStorage.create(newUser2);
        Integer userId2 = user2.getId();
        Friendship newFriendship = Friendship.builder()
                .user(user1)
                .userFriend(user2)
                .isConfirmed(false)
                .build();
        Friendship friendship = friendshipDbStorage.create(newFriendship);
        Integer id = friendship.getId();

        Optional<Friendship> getedFriendship = friendshipDbStorage.getById(id);

        assertTrue(getedFriendship.isPresent());
        assertEquals(id, getedFriendship.get().getId());
        assertEquals(userId1, getedFriendship.get().getUser().getId());
        assertEquals(userId2, getedFriendship.get().getUserFriend().getId());
    }

    @Test
    void create() {
        User user1 = userDbStorage.create(newUser1);
        Integer userId1 = user1.getId();
        User user2 = userDbStorage.create(newUser2);
        Integer userId2 = user2.getId();
        Friendship newFriendship = Friendship.builder()
                .user(user1)
                .userFriend(user2)
                .isConfirmed(false)
                .build();
        Friendship friendship = friendshipDbStorage.create(newFriendship);
        Integer id = friendship.getId();

        List<Friendship> friendshipList = friendshipDbStorage.get();

        assertNotNull(friendshipList);
        assertEquals(1, friendshipList.size());
        assertEquals(id, friendshipList.get(0).getId());
        assertEquals(userId1, friendshipList.get(0).getUser().getId());
        assertEquals(userId2, friendshipList.get(0).getUserFriend().getId());
    }

    @Test
    void createFriendshipOneSide() {
        User user1 = userDbStorage.create(newUser1);
        Integer userId1 = user1.getId();
        user1.setId(userId1);
        User user2 = userDbStorage.create(newUser2);
        Integer userId2 = user2.getId();
        user2.setId(userId2);

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
        User user1 = userDbStorage.create(newUser1);
        Integer userId1 = user1.getId();
        user1.setId(userId1);
        User user2 = userDbStorage.create(newUser2);
        Integer userId2 = user2.getId();
        user2.setId(userId2);

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
    void update() {
        User user1 = userDbStorage.create(newUser1);
        Integer userId1 = user1.getId();
        user1.setId(userId1);
        User user2 = userDbStorage.create(newUser2);
        Integer userId2 = user2.getId();
        user2.setId(userId2);
        Friendship newFriendship = Friendship.builder()
                .user(user1)
                .userFriend(user2)
                .isConfirmed(false)
                .build();
        Friendship friendship = friendshipDbStorage.create(newFriendship);
        Integer id = friendship.getId();
        Friendship friendshipForUpdate = Friendship.builder()
                .user(user1)
                .userFriend(user2)
                .isConfirmed(true)
                .build();

        Friendship updatedFriendship = friendshipDbStorage.update(id, friendshipForUpdate);

        assertEquals(userId1, friendship.getUser().getId());
        assertEquals(userId2, friendship.getUserFriend().getId());
        assertFalse(friendship.getIsConfirmed());
        assertEquals(userId1, updatedFriendship.getUser().getId());
        assertEquals(userId2, updatedFriendship.getUserFriend().getId());
        assertTrue(updatedFriendship.getIsConfirmed());
    }

    @Test
    void deleteFriendshipOneSide() {
        User user1 = userDbStorage.create(newUser1);
        Integer userId1 = user1.getId();
        user1.setId(userId1);
        User user2 = userDbStorage.create(newUser2);
        Integer userId2 = user2.getId();
        user2.setId(userId2);

        friendshipDbStorage.createFriendship(userId1, userId2);
        friendshipDbStorage.deleteFriendship(userId1, userId2);
        List<Friendship> friendshipList = friendshipDbStorage.get();

        assertNotNull(friendshipList);
        assertEquals(0, friendshipList.size());
    }

    @Test
    void deleteFriendshipTwoSide() {
        User user1 = userDbStorage.create(newUser1);
        Integer userId1 = user1.getId();
        user1.setId(userId1);
        User user2 = userDbStorage.create(newUser2);
        Integer userId2 = user2.getId();
        user2.setId(userId2);

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