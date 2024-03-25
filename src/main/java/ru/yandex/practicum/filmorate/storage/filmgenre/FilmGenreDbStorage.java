package ru.yandex.practicum.filmorate.storage.filmgenre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для {@link FilmGenre}.
 */
@Component("filmGenreDbStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFilmGenres(Integer filmId, List<Genre> genres) {
        List<Object[]> params = new ArrayList<>();
        String sql = "insert into filmgenre (film_id, genre_id)" +
                " values (?, ?)";

        for (Genre genre : genres) {
            params.add(new Object[]{filmId, genre.getId()});
        }

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params.toArray());

        jdbcTemplate.batchUpdate(sql, params);
    }

    @Override
    public void deleteFilmGenres(Integer filmId) {
        Object[] params = new Object[]{filmId};
        String sql = "delete from filmgenre where film_id = ?";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        jdbcTemplate.update(sql, params);
    }
}
