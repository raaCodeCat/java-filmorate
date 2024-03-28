package ru.yandex.practicum.filmorate.storage.mparating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;
import java.util.List;
import java.util.Optional;

/**
 * DAO для {@link MpaRating}.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MpaRatingDbStorage implements MpaRatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MpaRating> get() {
        Object[] params = new Object[]{};
        String sql = "select * from rating";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.query(sql, new MpaRatingMapper());
    }

    @Override
    public Optional<MpaRating> getById(Integer id) {
        Object[] params = new Object[]{id};
        String sql = "select * from rating where rating_id = ?";

        log.debug("Выполняется запрос к БД: {} Параметры: {}", sql, params);

        return jdbcTemplate.query(sql, new MpaRatingMapper(), params).stream().findAny();
    }
}
