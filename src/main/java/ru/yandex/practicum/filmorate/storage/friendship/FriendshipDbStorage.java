package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.mapper.FriendshipMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

/**
 * DAO для {@link Friendship}.
 */
@Component("friendshipDbStorage")
@Slf4j
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Friendship> get() {
        Object[] params = new Object[]{};
        String sql = "select * from friendship";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.query(sql, new FriendshipMapper());
    }

    public List<User> getFriendsByUserId(Integer id) {
        Object[] params = new Object[]{id};
        String sql = "select distinct friend.* " +
                "from friendship as fs " +
                "inner join users as friend on fs.user_fid = friend.user_id " +
                "where fs.user_id = ? " +
                "order by friend.user_id";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.query(sql, new UserMapper(), params);
    }

    @Override
    public Optional<Friendship> getById(Integer id) {
        Object[] params = new Object[]{id};
        String sql = "select * from friendship where friendship_id = ?";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.query(sql, new FriendshipMapper(), params).stream().findAny();
    }

    @Override
    public Friendship create(Friendship friendship) {
        Object[] params = new Object[]{
                friendship.getUser().getId(),
                friendship.getUserFriend().getId(),
                friendship.getIsConfirmed()
        };
        String sql = "insert into friendship(user_id, user_fid, friendship_isconfirmed) values (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"friendship_id"});
            ps.setInt(1, friendship.getUser().getId());
            ps.setInt(2, friendship.getUserFriend().getId());
            ps.setBoolean(3, friendship.getIsConfirmed());

            return ps;
        }, keyHolder);

        Integer friendshipId = (Integer) (keyHolder.getKey());

        return getById(friendshipId).orElse(null);
    }

    @Override
    @Transactional
    public void createFriendship(Integer userId, Integer friendId) {
        boolean isConfirmed = false;
        Optional<Friendship> friendshipRequestOptional = getByUserIdAndFriendId(friendId, userId);

        if (friendshipRequestOptional.isPresent()) {
            isConfirmed = true;
            Friendship friendshipRequest = friendshipRequestOptional.get();
            friendshipRequest.setIsConfirmed(isConfirmed);
            update(friendshipRequest.getId(), friendshipRequest);
        }

        Friendship friendship = new Friendship();
        User user = new User();
        User friend = new User();

        user.setId(userId);
        friend.setId(friendId);
        friendship.setUser(user);
        friendship.setUserFriend(friend);
        friendship.setIsConfirmed(isConfirmed);

        create(friendship);
    }

    @Override
    public Friendship update(Integer id, Friendship friendship) {
        Object[] params = new Object[]{
                friendship.getIsConfirmed(),
                id
        };
        String sql = "update friendship " +
                "set " +
                "friendship_isconfirmed = ? " +
                "where friendship_id = ?";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        jdbcTemplate.update(sql, params);

        return getById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteFriendship(Integer userId, Integer friendId) {
        Object[] params = new Object[]{userId, friendId};
        Optional<Friendship> friendshipRequestOptional = getByUserIdAndFriendId(friendId, userId);
        String sql = "delete from friendship where user_id = ? and user_fid = ?";

        if (friendshipRequestOptional.isPresent()) {
            Friendship friendshipRequest = friendshipRequestOptional.get();
            friendshipRequest.setIsConfirmed(false);
            update(friendshipRequest.getId(), friendshipRequest);
        }

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer otherUserId) {
        Object[] params = new Object[]{userId, otherUserId};
        String sql = "select u.* from friendship as fs " +
                "inner join users as u on fs.user_fid = u.user_id " +
                "where fs.user_id = ? " +
                "and user_fid in (select fs2.user_fid from friendship fs2 where fs2.user_id = ?)";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.query(sql, new UserMapper(), params);
    }

    private Optional<Friendship> getByUserIdAndFriendId(Integer userId, Integer friendId) {
        Object[] params = new Object[]{userId, friendId};
        String sql = "select * from friendship where user_id = ? and user_fid = ?";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.query(sql, new FriendshipMapper(), params).stream().findAny();
    }
}
