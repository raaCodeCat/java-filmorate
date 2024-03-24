package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.BaseService;

import java.util.List;

/**
 * Интерфейс сервиса для {@link Film}.
 */
public interface FilmService extends BaseService<Film> {
    Integer addLike(Integer id, Integer userId);

    Integer deleteLike(Integer id, Integer userId);

    List<Film> getPopularFilms(Integer count);
}
