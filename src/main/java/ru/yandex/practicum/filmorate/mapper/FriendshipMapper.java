package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Маппер для {@link Friendship}.
 */
public class FriendshipMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friendship friendship = new Friendship();
        User user = new User();
        User userFriend = new User();

        user.setId(rs.getObject("user_id", Integer.class));
        userFriend.setId(rs.getObject("user_fid", Integer.class));
        friendship.setId(rs.getObject("friendship_id", Integer.class));
        friendship.setUser(user);
        friendship.setUserFriend(userFriend);
        friendship.setIsConfirmed(rs.getBoolean("friendship_isconfirmed"));

        return friendship;
    }
}
