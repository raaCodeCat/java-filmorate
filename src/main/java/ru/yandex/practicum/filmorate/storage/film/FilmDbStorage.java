package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.resultextractor.FilmExtractor;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO для {@link Film}.
 */
@Component("filmDbStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private final String filmGetSql = "select " +
            "f.film_id, " +
            "f.film_name, " +
            "f.film_description, " +
            "f.film_releasedate, " +
            "f.film_duration, " +
            "r.rating_id, " +
            "r.rating_name, " +
            "r.rating_description, " +
            "g.genre_id, " +
            "g.genre_name, " +
            "(select" +
            "   group_concat(user_id separator ',') " +
            "   from filmlike t " +
            "   where t.film_id = f.film_id " +
            "   group by film_id " +
            ") as likes_str " +
            "from films as f " +
            "left join rating as r on f.rating_id = r.rating_id " +
            "left join filmgenre as fg on f.film_id = fg.film_id " +
            "left join genre as g on fg.genre_id = g.genre_id";

    @Override
    public List<Film> get() {
        Object[] params = new Object[]{};
        String sql = filmGetSql;

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.query(sql, new FilmExtractor());
    }

    @Override
    public Optional<Film> getById(Integer id) {
        Object[] params = new Object[]{id};
        String sql = filmGetSql + " where f.film_id = ? ";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        List<Film> films = jdbcTemplate.query(sql, new FilmExtractor(), params);

        if (films == null || films.size() == 0) {
            return Optional.empty();
        }

        return Optional.of(films.get(0));
    }

    @Override
    public Integer create(Film film) {
        Object[] params = new Object[]{
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                film.getMpa().getId()
        };
        String sql = "insert into " +
                "films (film_name, film_description, film_releasedate, film_duration, rating_id) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(film.getReleaseDate().atStartOfDay()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        return (Integer) (keyHolder.getKey());
    }

    @Override
    public void update(Integer id, Film film) {
        Object[] params = new Object[]{
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                id
        };
        String sql = "update films " +
                "set " +
                "film_name = ?, " +
                "film_description = ?, " +
                "film_releasedate = ?, " +
                "film_duration = ?, " +
                "rating_id = ? " +
                "where film_id = ?";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        jdbcTemplate.update(sql, params);
    }


    @Override
    public List<Film> getPopularFilms(Integer count) {
        Object[] params = new Object[]{
                count
        };
        String sql = "select " +
                "f.film_id " +
                "from films as f " +
                "left join filmlike as fl on f.film_id = fl.film_id " +
                "group by " +
                "f.film_id " +
                "order by count(distinct fl.user_id) desc, f.film_id " +
                "limit ?";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        List<Integer> popularFilmsId = jdbcTemplate.queryForList(sql, Integer.class, params);
        List<Film> popularFilmsResult = new ArrayList<>();

        for (Integer id : popularFilmsId) {
            getById(id).ifPresent(popularFilmsResult::add);
        }

        return popularFilmsResult;
    }
}
