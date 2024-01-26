package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmControllerTest {
    private FilmController filmController;
    private Film film;
    private Film filmToUpdate;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();

        filmToUpdate = Film.builder()
                .id(1)
                .name("Film Updated")
                .description("New film update decription")
                .releaseDate(LocalDate.of(1989, 4, 17))
                .duration(190)
                .build();
    }

    private void addFilmToList() {
        filmController.addFilm(film);
    }

    @Test
    @DisplayName("Добавления фильма, все поля валидные")
    void addFilm_allFieldsValid() {
        filmController.addFilm(film);

        List<Film> films = filmController.getAllFilms();
        final Film actual = films.get(0);

        assertNotNull(films);
        assertEquals(1, films.size());
        assertEquals(film, actual);
    }

    @Test
    @DisplayName("Добавления фильма, дата релиза (releaseDate) 28 декабря 1895 года")
    void addFilm_releaseDateEquals28December1895() {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        filmController.addFilm(film);

        List<Film> films = filmController.getAllFilms();
        final Film actual = films.get(0);

        assertNotNull(films);
        assertEquals(1, films.size());
        assertEquals(film, actual);
    }

    @Test
    @DisplayName("Добавления фильма, дата релиза (releaseDate) до 28 декабря 1895 года")
    void addFilm_fail_releaseDateBefore28December1895() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );

        final List<Film> films = filmController.getAllFilms();

        assertNotNull(films);
        assertEquals(0, films.size());
        assertEquals("400 BAD_REQUEST \"Параметр releaseDate не должна быть меньше 28 декабря 1895 года\"",
                exception.getMessage());
    }

    @Test
    @DisplayName("Обновление фильма, все поля валидные")
    void updateFilm_allFieldsValid() {
        addFilmToList();
        filmController.updateFilm(filmToUpdate);

        List<Film> films = filmController.getAllFilms();
        final Film actual = films.get(0);

        assertNotNull(films);
        assertEquals(1, films.size());
        assertEquals(filmToUpdate, actual);
    }

    @Test
    @DisplayName("Обновление фильма, не правильный id")
    void updateFilm_fail_invalidId() {
        addFilmToList();
        filmToUpdate.setId(777);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.updateFilm(filmToUpdate)
        );

        assertEquals("404 NOT_FOUND \"Фильм с id = 777 не найден\"",
                exception.getMessage());
    }
}