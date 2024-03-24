package ru.yandex.practicum.filmorate.service;

import java.util.List;

public interface BaseGetService<T> {
    List<T> get();

    T getById(Integer id);
}
