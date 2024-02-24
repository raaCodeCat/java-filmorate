package ru.yandex.practicum.filmorate.service;

import java.util.List;

/**
 * Интерфейс базового сервиса.
 */
public interface BaseService<T> {
    List<T> get();

    T getById(Integer id);

    T create(T t);

    T update(T t);
}
