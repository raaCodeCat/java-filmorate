package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import java.util.List;

/**
 * Хранилище для {@link Film}.
 */
public interface FilmStorage extends BaseStorage<Film> {
    Integer addLike(Integer id, Integer userId);

    Integer deleteLike(Integer id, Integer userId);

    List<Film> getPopularFilms(Integer count);
}
