package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

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
    private String email;

    /**
     * Логин пользователя.
     */
    private String login;

    /**
     * Имя для отображения.
     */
    private String name;

    /**
     * Дата рождения.
     */
    private LocalDate birthday;
}
