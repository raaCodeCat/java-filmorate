package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для {@link Film}
 */
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer filmIdCounter = 1;

    /**
     * Добавление фильма.
     */
    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.debug("Получен запрос POST /films.");
        log.debug("Попытка добавить фильм {}.", film);

        validateFilmFields(film);

        Integer id = filmIdCounter++;

        log.debug("Фильму {} назначен id = {}.", film, id);

        film.setId(id);
        films.put(id, film);

        log.debug("Добавлен {} добавлен.", film);

        return film;
    }

    /**
     * Обновление фильма.
     */
    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.debug("Получен запрос POST /films.");
        log.debug("Попытка обновить фильм {}.", film);

        validateFilmFields(film);
        checkFilmExistsById(film);

        Integer id = film.getId();

        if (films.containsKey(id)) {
            films.replace(id, film);
        }

        log.debug("Фильм {} обновлен.", film);

        return films.get(id);
    }

    /**
     * Получение всех фильмов.
     */
    @GetMapping
    public List<Film> getAllFilms() {
        List<Film> filmList = new ArrayList<>();

        for (Integer key : films.keySet()) {
            filmList.add(films.get(key));
        }

        return List.copyOf(filmList);
    }

    private void checkFilmExistsById(Film film) {
        Integer id = film.getId();
        if (!films.containsKey(id)) {
            log.debug("Не найден фильм для обновления с id = {}", id);

            throw new ValidationException(HttpStatus.NOT_FOUND, "Фильм с id = " + id + "не найден");
        }
    }

    private void validateFilmFields(Film film) {
        String name = film.getName();
        String description = film.getDescription();
        Integer duration = film.getDuration();
        LocalDate releaseDate = film.getReleaseDate();

        if (name == null || "".equals(name)) {
            log.debug("Не пройдена валидация name: {}", name);

            throw new ValidationException(HttpStatus.BAD_REQUEST, "Параметр name не должен быть пустым");
        }

        if (description != null && description.length() > 200) {
            log.debug("Не пройдена валидация description: {}", description);

            throw new ValidationException(HttpStatus.BAD_REQUEST, "Максимальная длинна параметра description должна быть 200 символов");
        }

        if (duration != null && duration < 0) {
            log.debug("Не пройдена валидация duration: {}", duration);

            throw new ValidationException(HttpStatus.BAD_REQUEST, "Параметр duration должен быть положительным");
        }

        if (releaseDate != null && releaseDate.isBefore(LocalDate.parse("28.12.1895",
                DateTimeFormatter.ofPattern("dd.MM.yyyy")))) {
            log.debug("Не пройдена валидация releaseDate: {}", releaseDate);

            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Параметр releaseDate не должна быть меньше 28 декабря 1895 года");
        }
    }
}
