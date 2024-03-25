package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import java.util.List;

/**
 * Хранилище для {@link Film}.
 */
public interface FilmStorage extends BaseStorage<Film> {
    List<Film> getPopularFilms(Integer count);
}
