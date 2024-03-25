package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс базового хранилища.
 */
public interface BaseStorage<T> {
    List<T> get();

    Optional<T> getById(Integer id);

    Integer create(T t);

    void update(Integer id, T t);
}
