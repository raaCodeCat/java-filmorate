package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
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
    @NotBlank(message = "Параметр name не может быть пустым.")
    private String name;

    /**
     * Описание фильма.
     */
    @Length(max = 200, message = "Максимальная длина описания — 200 символов.")
    private String description;

    /**
     * Дата релиза.
     */
    private LocalDate releaseDate;

    /**
     * Продолжительность фильма
     */
    @Min(value = 1, message = "Продолжительность фильма должна быть положительной.")
    private Integer duration;
}
