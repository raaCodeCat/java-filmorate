package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Builder
@ToString
public class Film {
    /**
     * Целочисленный идентификатор фильма.
     */
    private Integer id;

    /**
     * Название фильма.
     */
    private String name;

    /**
     * Описание фильма.
     */
    private String description;

    /**
     * Дата релиза.
     */
    private LocalDate releaseDate;

    /**
     * Продолжительность фильма
     */
    private Integer duration;
}
