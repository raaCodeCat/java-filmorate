package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Базовая таблица.
 */
@Getter
@Setter
public class CommonTable {
    /**
     * Идентификатор записи.
     */
    private Integer id;

    /**
     * Дата добавления записи.
     */
    private LocalDateTime insDT;

    /**
     * Дата обновления записи.
     */
    private LocalDateTime updDT;
}
