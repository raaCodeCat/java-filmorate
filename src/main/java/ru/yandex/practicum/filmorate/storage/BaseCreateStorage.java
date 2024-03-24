package ru.yandex.practicum.filmorate.storage;

/**
 * Интерфейс хранилища.
 * Содержит методы добавления данных.
 */
public interface BaseCreateStorage<T> {
    T create(T t);
}
