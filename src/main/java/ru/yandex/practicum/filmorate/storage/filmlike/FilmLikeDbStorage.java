package ru.yandex.practicum.filmorate.storage.filmlike;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmLikeMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DAO для {@link FilmLike}.
 */
@Component("filmLikeDbStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmLikeDbStorage implements FilmLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<FilmLike> get() {
        Object[] params = new Object[]{};
        String sql = "select * from filmlike";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.query(sql, new FilmLikeMapper());
    }

    public List<User> getLikedUsersByFilmId(Integer id) {
        Object[] params = new Object[]{id};
        String sql = "select distinct u.* " +
                "from filmlike as fl " +
                "inner join users as u on fl.user_id = u.user_id " +
                "where fl.film_id = ?";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.query(sql, new UserMapper(), params);
    }

    public Integer getLikesCountByFilmId(Integer id) {
        Object[] params = new Object[]{id};
        String sql = "select count(distinct user_id) as likeCount from filmlike where film_id = ?";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.queryForObject(sql, Integer.class, params);
    }

    public Set<Integer> getLikedUsersIdByFilmId(Integer id) {
        Set<Integer> likedUsersId = new HashSet<>();
        List<User> users = getLikedUsersByFilmId(id);

        for (User user : users) {
            likedUsersId.add(user.getId());
        }

        return likedUsersId;
    }

    @Override
    public Integer addLike(Integer id, Integer userId) {
        Object[] params = new Object[]{
                id,
                userId
        };
        String sql = "insert into " +
                "filmlike (film_id, user_id) " +
                "values (?, ?)";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        jdbcTemplate.update(sql, params);

        return getLikesCountByFilmId(id);
    }

    @Override
    public Integer deleteLike(Integer id, Integer userId) {
        Object[] params = new Object[]{
                id,
                userId
        };
        String sql = "delete from filmlike where film_id = ? and user_id = ?";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        jdbcTemplate.update(sql, params);

        return getLikesCountByFilmId(id);
    }
}
