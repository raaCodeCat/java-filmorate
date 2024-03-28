package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;
import java.util.Optional;

/**
 * DAO для {@link Genre}.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> get() {
        Object[] params = new Object[]{};
        String sql = "select * from genre";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.query(sql, new GenreMapper());
    }

    @Override
    public Optional<Genre> getById(Integer id) {
        Object[] params = new Object[]{id};
        String sql = "select * from genre where genre_id = ?";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.query(sql, new GenreMapper(), params).stream().findAny();
    }
}
