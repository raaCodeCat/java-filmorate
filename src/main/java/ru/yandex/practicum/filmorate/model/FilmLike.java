package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Лайки фильмов.
 */
@Getter
@Setter
public class FilmLike {
    /**
     * Идентификатор записи.
     */
    private Integer id;

    /**
     * Фильм.
     */
    private Film film;

    /**
     * Пользователь.
     */
    private User user;
}
