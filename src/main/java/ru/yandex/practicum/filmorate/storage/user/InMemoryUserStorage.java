package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

/**
 * Хранилище в памяти для {@link User}.
 */
@Component("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    private Integer userIdCounter = 1;

    @Override
    public List<User> get() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(Integer id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        }

        return Optional.empty();
    }

    @Override
    public User create(User user) {
        Integer id = userIdCounter++;
        log.debug("Пользователю {} назначен id = {}.", user, id);

        user.setId(id);
        users.put(id, user);

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }

        return users.get(id);
    }

    @Override
    public User update(Integer id, User user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }

        users.replace(id, user);

        return users.get(id);
    }

    @Override
    public void addToFriends(Integer id, Integer friendId) {
        Set<Integer> friends = users.get(id).getFriends();
        friends.add(friendId);

        friends = users.get(friendId).getFriends();
        friends.add(id);
    }

    @Override
    public void deleteFromFriends(Integer id, Integer friendId) {
        Set<Integer> friends = users.get(id).getFriends();
        friends.remove(friendId);

        friends = users.get(friendId).getFriends();
        friends.remove(id);
    }

    @Override
    public List<User> getUserFriends(Integer id) {
        Set<Integer> friendsId = users.get(id).getFriends();

        return getUserListByIds(friendsId);
    }

    @Override
    public List<User> getMutualFriends(Integer id, Integer otherId) {
        Set<Integer> friendsId = users.get(id).getFriends();
        Set<Integer> otherFriendsId = users.get(otherId).getFriends();
        Set<Integer> friendsIdTmp = new HashSet<>(friendsId);

        friendsIdTmp.retainAll(otherFriendsId);

        return getUserListByIds(friendsIdTmp);
    }

    private List<User> getUserListByIds(Set<Integer> ids) {
        List<User> userList = new ArrayList<>();

        for (Integer id : ids) {
            userList.add(users.get(id));
        }

        return userList;
    }
}
