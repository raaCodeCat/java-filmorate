package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для {@link Film}
 */
@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
@Validated
public class FilmController {
    private final FilmService filmService;

    /**
     * Добавление фильма.
     */
    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.debug("Получен запрос POST /films.");

        return filmService.create(film);
    }

    /**
     * Обновление фильма.
     */
    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.debug("Получен запрос PUT /films.");

        return filmService.update(film);
    }

    /**
     * Добавление лайка фильму.
     */
    @PutMapping("/{id}/like/{userId}")
    public Map<String, Integer> addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        return Map.of("likeCount", filmService.addLike(id, userId));
    }

    /**
     * Удаление лайка у фильма.
     */
    @DeleteMapping("/{id}/like/{userId}")
    public Map<String, Integer> deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        return Map.of("likeCount", filmService.deleteLike(id, userId));
    }

    /**
     * Получение всех фильмов.
     */
    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.get();
    }

    /**
     * Получение фильма по идентификатору (id).
     */
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmService.getById(id);
    }

    /**
     * Возвращает список из первых count фильмов по количеству лайков.
     * Если значение параметра count не задано, верните первые 10.
     */
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") @Min(1) Integer count) {
        return filmService.getPopularFilms(count);
    }
}
