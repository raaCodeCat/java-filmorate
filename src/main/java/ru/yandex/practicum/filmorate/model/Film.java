package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.ToString;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
@ToString
public class Film {
    private Integer id;

    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
}
