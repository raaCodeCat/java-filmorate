package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

/**
 * User
 */
@Data
@ToString
@Builder
public class User {
    /**
     * Целочисленный идентификатор пользователя.
     */
    private Integer id;

    /**
     * Электронная почта.
     */
    @NotBlank(message = "Параметр email не может быть пустым.")
    @Email(message = "Неправильный формат email адреса.")
    private String email;

    /**
     * Логин пользователя.
     */
    @NotBlank(message = "Параметр login не может быть пустым.")
    private String login;

    /**
     * Имя для отображения.
     */
    private String name;

    /**
     * Дата рождения.
     */
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;
}
