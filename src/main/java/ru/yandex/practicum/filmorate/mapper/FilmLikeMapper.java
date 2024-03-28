package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmLikeMapper implements RowMapper<FilmLike> {
    @Override
    public FilmLike mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        User user = new User();
        FilmLike filmLike = new FilmLike();

        film.setId(rs.getObject("film_id", Integer.class));
        user.setId(rs.getObject("user_id", Integer.class));

        filmLike.setId(rs.getObject("filmlike_id", Integer.class));
        filmLike.setFilm(film);
        filmLike.setUser(user);

        return filmLike;
    }
}
