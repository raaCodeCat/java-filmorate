package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Дружба.
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {
    /**
     * Идентификатор записи.
     */
    private Integer id;

    /**
     * Пользователь.
     */
    private User user;

    /**
     * Друг пользователя.
     */
    private User userFriend;

    /**
     * Статус дружбы.
     */
    private Boolean isConfirmed;
}
