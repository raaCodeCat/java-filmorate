package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.resultextractor.FilmExtractor;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeStorage;
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

    private final FilmLikeStorage filmLikeStorage;

    private final FilmGenreStorage filmGenreStorage;

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
            "g.genre_name " +
            "from films as f " +
            "left join rating as r on f.rating_id = r.rating_id " +
            "left join filmgenre as fg on f.film_id = fg.film_id " +
            "left join genre as g on fg.genre_id = g.genre_id";

    @Override
    public List<Film> get() {
        Object[] params = new Object[]{};
        String sql = filmGetSql;

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        List<Film> films = jdbcTemplate.query(sql, new FilmExtractor());

        addLikesInfoToFilmList(films);

        return films;
    }

    @Override
    public Optional<Film> getById(Integer id) {
        Object[] params = new Object[]{id};
        String sql = filmGetSql + " where f.film_id = ? ";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        List<Film> films = jdbcTemplate.query(sql, new FilmExtractor(), params);
        addLikesInfoToFilmList(films);

        if (films == null || films.size() == 0) {
            return Optional.empty();
        }

        return Optional.of(films.get(0));
    }

    @Override
    @Transactional
    public Film create(Film film) {
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

        Integer filmId = (Integer) (keyHolder.getKey());

        List<Genre> genres = film.getGenres();

        if (genres != null && genres.size() > 0) {
            filmGenreStorage.addFilmGenres(filmId, genres);
        }

        return getById(filmId).orElse(null);
    }

    @Override
    @Transactional
    public Film update(Integer id, Film film) {
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

        jdbcTemplate.update(
                sql,
                params
        );

        List<Genre> genres = film.getGenres();

        filmGenreStorage.deleteFilmGenres(id);

        if (genres != null && genres.size() > 0) {
            filmGenreStorage.addFilmGenres(id, genres);
        }

        return getById(id).orElse(null);
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

        return filmLikeStorage.getLikesCountByFilmId(id);
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

        return filmLikeStorage.getLikesCountByFilmId(id);
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

    private void addLikesInfoToFilmList(List<Film> films) {
        if (films == null) {
            return;
        }

        for (Film film : films) {
            film.setLikedUsers(filmLikeStorage.getLikedUsersIdByFilmId(film.getId()));
        }
    }
}
