package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;

/**
 * User
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
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

    /**
     * Список друзей.
     */
    private Set<Integer> friends;

    /**
     * Параметр login не должен содержать пробелы.
     */
    @AssertTrue(message = "Параметр login не должен содержать пробелы")
    private boolean isLoginWithOutWhitespace() {
        if (login != null) {
            return login.replaceAll("\\s", "").equals(login);
        }

        return true;
    }
}
