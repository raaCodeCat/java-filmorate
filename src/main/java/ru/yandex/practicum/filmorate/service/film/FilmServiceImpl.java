package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для {@link Film}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    private final UserService userService;

    @Override
    public List<Film> get() {
        return filmStorage.get();
    }

    @Override
    public Film getById(Integer id) {
        Optional<Film> filmOptional = filmStorage.getById(id);

        if (filmOptional.isEmpty()) {
            throw new ValidationException(HttpStatus.NOT_FOUND, "Фильм с id = " + id + " не найден");
        }

        return filmOptional.get();
    }

    @Override
    public Film create(Film film) {
        Film filmNew = filmStorage.create(film);
        log.debug("Добавлен фильм {}.", film);

        return filmNew;
    }

    @Override
    public Film update(Film film) {
        log.debug("Попытка обновить фильм {}.", film);

        Integer id = film.getId();
        checkExistsFilmById(id);

        Film filmUpdated = filmStorage.update(id, film);
        log.debug("Фильм {} обновлен.", film);

        return filmUpdated;
    }

    @Override
    public Integer addLike(Integer id, Integer userId) {
        checkExistsFilmById(id);
        checkExistsUserById(userId);

        return filmStorage.addLike(id, userId);
    }

    @Override
    public Integer deleteLike(Integer id, Integer userId) {
        checkExistsFilmById(id);
        checkExistsUserById(userId);

        return filmStorage.deleteLike(id, userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

    private void checkExistsFilmById(Integer id) {
        Optional<Film> filmOptional = filmStorage.getById(id);

        if (filmOptional.isEmpty()) {
            log.debug("Не найден фильм для обновления с id = {}", id);

            throw new ValidationException(HttpStatus.NOT_FOUND, "Фильм с id = " + id + " не найден");
        }
    }

    private void checkExistsUserById(Integer id) {
        userService.getById(id);
    }
}
