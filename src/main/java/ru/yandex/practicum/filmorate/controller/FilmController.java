package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
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
        log.debug("Получен запрос PUT /films.");
        log.debug("Попытка обновить фильм {}.", film);

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
        return new ArrayList<>(films.values());
    }

    private void checkFilmExistsById(Film film) {
        Integer id = film.getId();
        if (!films.containsKey(id)) {
            log.debug("Не найден фильм для обновления с id = {}", id);

            throw new ValidationException(HttpStatus.NOT_FOUND, "Фильм с id = " + id + " не найден");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }
}
