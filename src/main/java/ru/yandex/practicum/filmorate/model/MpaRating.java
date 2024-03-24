package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Возрастной рейтинг.
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class MpaRating {
    /**
     * Идентификатор записи.
     */
    private Integer id;

    /**
     * Код.
     */
    private String name;

    /**
     * Описание.
     */
    private String description;
}
