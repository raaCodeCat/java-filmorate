package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс хранилища.
 * Содержит методы получения данных.
 */
public interface BaseGetStorage<T> {
    List<T> get();

    Optional<T> getById(Integer id);
}
