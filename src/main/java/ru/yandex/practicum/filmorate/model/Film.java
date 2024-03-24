package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Film.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
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
     * Продолжительность фильма.
     */
    @Min(value = 1, message = "Продолжительность фильма должна быть положительной.")
    private Integer duration;

    /**
     * Рейтинг.
     */
    private MpaRating mpa;

    /**
     * Список жанров.
     */
    private List<Genre> genres;

    /**
     * Пользователи поставившие лайк.
     */
    private Set<Integer> likedUsers;

    /**
     * Дата релиза фильма не должна быть меньше 28 декабря 1895 года.
     */
    @AssertTrue(message = "Параметр releaseDate не должен быть меньше 28 декабря 1895 года")
    private boolean isDateAfterOrEquals28Dec1985() {
        if (releaseDate != null) {
            return !releaseDate.isBefore(LocalDate.of(1895, 12, 28));
        }

        return true;
    }
}
