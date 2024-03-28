package ru.yandex.practicum.filmorate.storage.filmgenre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmGenreDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;
    private FilmGenreDbStorage filmGenreDbStorage;
    private Film film;

    @BeforeEach
    void setUp() {
        filmGenreDbStorage = new FilmGenreDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate);

        MpaRating mpa =  MpaRating.builder().id(1).build();

        film = Film.builder()
                .name("FilmName")
                .description("FilmDescription")
                .releaseDate(LocalDate.of(2020, 1, 2))
                .duration(100)
                .mpa(mpa)
                .build();
    }

    @Test
    void addFilmGenres() {
        Genre genre1 = Genre.builder().id(1).build();
        Genre genre2 = Genre.builder().id(3).build();
        Integer filmId = filmDbStorage.create(film);

        Optional<Film> filmBeforeAddGenre = filmDbStorage.getById(filmId);
        filmGenreDbStorage.addFilmGenres(filmId, Set.of(genre1, genre2));
        Optional<Film> filmAfterAddGenre = filmDbStorage.getById(filmId);

        assertTrue(filmBeforeAddGenre.isPresent());
        assertNull(filmBeforeAddGenre.get().getGenres());
        assertTrue(filmAfterAddGenre.isPresent());
        assertNotNull(filmAfterAddGenre.get().getGenres());
        assertEquals(2, filmAfterAddGenre.get().getGenres().size());
    }

    @Test
    void deleteFilmGenres() {
        Genre genre1 = Genre.builder().id(1).build();
        Genre genre2 = Genre.builder().id(3).build();
        Integer filmId = filmDbStorage.create(film);
        filmGenreDbStorage.addFilmGenres(filmId, Set.of(genre1, genre2));

        Optional<Film> filmBeforeDelGenre = filmDbStorage.getById(filmId);
        filmGenreDbStorage.deleteFilmGenres(filmId);
        Optional<Film> filmAfterDelGenre = filmDbStorage.getById(filmId);

        assertTrue(filmBeforeDelGenre.isPresent());
        assertNotNull(filmBeforeDelGenre.get().getGenres());
        assertEquals(2, filmBeforeDelGenre.get().getGenres().size());
        assertTrue(filmAfterDelGenre.isPresent());
        assertNull(filmAfterDelGenre.get().getGenres());
    }
}