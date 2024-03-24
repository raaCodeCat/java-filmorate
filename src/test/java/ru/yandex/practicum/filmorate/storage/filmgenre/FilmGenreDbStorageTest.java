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
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeDbStorage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
        filmDbStorage = new FilmDbStorage(
                jdbcTemplate,
                new FilmLikeDbStorage(jdbcTemplate),
                new FilmGenreDbStorage(jdbcTemplate)
        );

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
        Film addedFilm = filmDbStorage.create(film);
        Integer filmId = addedFilm.getId();

        Optional<Film> filmBeforeAddGenre = filmDbStorage.getById(filmId);
        filmGenreDbStorage.addFilmGenres(filmId, List.of(genre1, genre2));
        Optional<Film> filmAfterAddGenre = filmDbStorage.getById(filmId);

        assertTrue(filmBeforeAddGenre.isPresent());
        assertNull(filmBeforeAddGenre.get().getGenres());
        assertTrue(filmAfterAddGenre.isPresent());
        assertNotNull(filmAfterAddGenre.get().getGenres());
        assertEquals(2, filmAfterAddGenre.get().getGenres().size());
        assertEquals(1, filmAfterAddGenre.get().getGenres().get(0).getId());
        assertEquals("Комедия", filmAfterAddGenre.get().getGenres().get(0).getName());
        assertEquals(3, filmAfterAddGenre.get().getGenres().get(1).getId());
        assertEquals("Мультфильм", filmAfterAddGenre.get().getGenres().get(1).getName());
    }

    @Test
    void deleteFilmGenres() {
        Genre genre1 = Genre.builder().id(1).build();
        Genre genre2 = Genre.builder().id(3).build();
        Film addedFilm = filmDbStorage.create(film);
        Integer filmId = addedFilm.getId();
        filmGenreDbStorage.addFilmGenres(filmId, List.of(genre1, genre2));

        Optional<Film> filmBeforeDelGenre = filmDbStorage.getById(filmId);
        filmGenreDbStorage.deleteFilmGenres(filmId);
        Optional<Film> filmAfterDelGenre = filmDbStorage.getById(filmId);

        assertTrue(filmBeforeDelGenre.isPresent());
        assertNotNull(filmBeforeDelGenre.get().getGenres());
        assertEquals(2, filmBeforeDelGenre.get().getGenres().size());
        assertEquals(1, filmBeforeDelGenre.get().getGenres().get(0).getId());
        assertEquals("Комедия", filmBeforeDelGenre.get().getGenres().get(0).getName());
        assertEquals(3, filmBeforeDelGenre.get().getGenres().get(1).getId());
        assertEquals("Мультфильм", filmBeforeDelGenre.get().getGenres().get(1).getName());
        assertTrue(filmAfterDelGenre.isPresent());
        assertNull(filmAfterDelGenre.get().getGenres());
    }
}