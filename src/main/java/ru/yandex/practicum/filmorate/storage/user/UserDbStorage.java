package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
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
    public Integer create(User user) {
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

        return (Integer) (keyHolder.getKey());
    }

    @Override
    public void update(Integer id, User user) {
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

        jdbcTemplate.update(sql, params);
    }
}
