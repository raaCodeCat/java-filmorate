package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;
    private UserDbStorage userDbStorage;

    private FilmLikeDbStorage filmLikeDbStorage;
    private Film firstFilm;
    private Film secondFilm;
    private User newUser;

    @BeforeEach
    void setUp() {
        filmDbStorage = new FilmDbStorage(jdbcTemplate);

        userDbStorage = new UserDbStorage(jdbcTemplate);

        filmLikeDbStorage = new FilmLikeDbStorage(jdbcTemplate);

        MpaRating mpa =  MpaRating.builder().id(1).build();
        Genre genre = Genre.builder().id(2).build();
        firstFilm = Film.builder()
                .name("FilmName")
                .description("FilmDescription")
                .releaseDate(LocalDate.of(2020, 1, 2))
                .duration(100)
                .mpa(mpa)
                .genres(Set.of(genre))
                .build();

        MpaRating mpa2 = MpaRating.builder().id(2).build();
        Genre genre1 = Genre.builder().id(1).build();
        Genre genre2 = Genre.builder().id(3).build();
        secondFilm = Film.builder()
                .name("UpdateName")
                .description("UpdateDescription")
                .releaseDate(LocalDate.of(2021, 1, 2))
                .duration(60)
                .mpa(mpa2)
                .genres(Set.of(genre1, genre2))
                .build();

        newUser = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
    }

    @Test
    void get() {
        filmDbStorage.create(firstFilm);

        List<Film> films = filmDbStorage.get();

        assertEquals(1, films.size());
        assertEquals("FilmName", films.get(0).getName());
        assertEquals("FilmDescription", films.get(0).getDescription());
        assertEquals(LocalDate.of(2020, 1, 2), films.get(0).getReleaseDate());
        assertEquals(100, films.get(0).getDuration());
        assertEquals(1, films.get(0).getMpa().getId());
        assertEquals("G", films.get(0).getMpa().getName());
        assertEquals("Нет возрастных ограничений", films.get(0).getMpa().getDescription());
    }

    @Test
    void getById() {
        Integer filmId = filmDbStorage.create(firstFilm);

        Optional<Film> filmOptional = filmDbStorage.getById(filmId);

        assertTrue(filmOptional.isPresent());
        assertEquals(filmId, filmOptional.get().getId());
        assertEquals("FilmName", filmOptional.get().getName());
        assertEquals("FilmDescription", filmOptional.get().getDescription());
        assertEquals(LocalDate.of(2020, 1, 2), filmOptional.get().getReleaseDate());
        assertEquals(100, filmOptional.get().getDuration());
        assertEquals(1, filmOptional.get().getMpa().getId());
        assertEquals("G", filmOptional.get().getMpa().getName());
        assertEquals("Нет возрастных ограничений", filmOptional.get().getMpa().getDescription());
    }

    @Test
    void create() {
        Integer filmId = filmDbStorage.create(firstFilm);

        Optional<Film> filmOptional = filmDbStorage.getById(filmId);

        assertTrue(filmOptional.isPresent());
        assertEquals(filmId, filmOptional.get().getId());
        assertEquals("FilmName", filmOptional.get().getName());
        assertEquals("FilmDescription", filmOptional.get().getDescription());
        assertEquals(LocalDate.of(2020, 1, 2), filmOptional.get().getReleaseDate());
        assertEquals(100, filmOptional.get().getDuration());
        assertEquals(1, filmOptional.get().getMpa().getId());
        assertEquals("G", filmOptional.get().getMpa().getName());
        assertEquals("Нет возрастных ограничений", filmOptional.get().getMpa().getDescription());
    }

    @Test
    void update() {
        Integer filmId = filmDbStorage.create(firstFilm);

        filmDbStorage.update(filmId, secondFilm);
        Optional<Film> filmOptional = filmDbStorage.getById(filmId);

        assertTrue(filmOptional.isPresent());
        assertEquals(filmId, filmOptional.get().getId());
        assertEquals("UpdateName", filmOptional.get().getName());
        assertEquals("UpdateDescription", filmOptional.get().getDescription());
        assertEquals(LocalDate.of(2021, 1, 2), filmOptional.get().getReleaseDate());
        assertEquals(60, filmOptional.get().getDuration());
        assertEquals(2, filmOptional.get().getMpa().getId());
        assertEquals("PG", filmOptional.get().getMpa().getName());
        assertEquals("Детям рекомендуется смотреть фильм с родителями", filmOptional.get().getMpa().getDescription());
    }

    @Test
    void getPopularFilms() {
        Integer filmId1 = filmDbStorage.create(firstFilm);
        Integer filmId2 = filmDbStorage.create(secondFilm);
        Integer userId = userDbStorage.create(newUser);

        List<Film> popularFilmBeforeLike = filmDbStorage.getPopularFilms(2);
        filmLikeDbStorage.addLike(filmId2, userId);
        List<Film> popularFilmAfterLike = filmDbStorage.getPopularFilms(2);

        assertEquals(2, popularFilmBeforeLike.size());
        assertEquals(filmId1, popularFilmBeforeLike.get(0).getId());
        assertEquals(filmId2, popularFilmBeforeLike.get(1).getId());
        assertEquals(filmId2, popularFilmAfterLike.get(0).getId());
        assertEquals(filmId1, popularFilmAfterLike.get(1).getId());
    }
}