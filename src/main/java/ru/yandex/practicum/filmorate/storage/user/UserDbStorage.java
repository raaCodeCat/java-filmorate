package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * DAO для {@link User}.
 */
@Component("userDbStorage")
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Qualifier("friendshipDbStorage")
    private final FriendshipStorage friendshipStorage;

    @Override
    public List<User> get() {
        Object[] params = new Object[]{};
        String sql = "select * from users";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public Optional<User> getById(Integer id) {
        Object[] params = new Object[]{id};
        String sql = "select * from users where user_id = ?";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.query(sql, new UserMapper(), params).stream().findAny();
    }

    @Override
    public User create(User user) {
        Object[] params = new Object[]{
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Timestamp.valueOf(user.getBirthday().atStartOfDay()),
        };
        String sql = "insert into users (user_email, user_login, user_name, user_birthday) " +
                "values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setTimestamp(4, Timestamp.valueOf(user.getBirthday().atStartOfDay()));

            return ps;
        }, keyHolder);

        Integer userId = (Integer) (keyHolder.getKey());

        return getById(userId).orElse(null);
    }

    @Override
    public User update(Integer id, User user) {
        Object[] params = new Object[]{
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                id
        };
        String sql = "update users " +
                "set " +
                "user_email = ?, " +
                "user_login = ?, " +
                "user_name = ?, " +
                "user_birthday = ? " +
                "where user_id = ?";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        jdbcTemplate.update(
                sql,
                params
        );

        return getById(id).orElse(null);
    }

    @Override
    @Transactional
    public void addToFriends(Integer id, Integer friendId) {
        friendshipStorage.createFriendship(id, friendId);
    }

    @Override
    @Transactional
    public void deleteFromFriends(Integer id, Integer friendId) {
        friendshipStorage.deleteFriendship(id, friendId);
    }

    @Override
    public List<User> getUserFriends(Integer id) {
        return friendshipStorage.getFriendsByUserId(id);
    }

    @Override
    public List<User> getMutualFriends(Integer id, Integer otherId) {
        return friendshipStorage.getMutualFriends(id, otherId);
    }
}
