package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Жанры фильма.
 */
@Getter
@Setter
public class FilmGenre {
    /**
     * Идентификатор записи.
     */
    private Integer id;

    /**
     * Фильм.
     */
    private Film film;

    /**
     * Жанр.
     */
    private Genre genre;
}
