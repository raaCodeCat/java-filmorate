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
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;
    private UserDbStorage userDbStorage;
    private Film firstFilm;
    private Film secondFilm;
    private User newUser;

    @BeforeEach
    void setUp() {
        filmDbStorage = new FilmDbStorage(
                jdbcTemplate,
                new FilmLikeDbStorage(jdbcTemplate),
                new FilmGenreDbStorage(jdbcTemplate)
        );

        userDbStorage = new UserDbStorage(
                jdbcTemplate,
                new FriendshipDbStorage(jdbcTemplate)
        );

        MpaRating mpa =  MpaRating.builder().id(1).build();
        Genre genre = Genre.builder().id(2).build();
        firstFilm = Film.builder()
                .name("FilmName")
                .description("FilmDescription")
                .releaseDate(LocalDate.of(2020, 1, 2))
                .duration(100)
                .mpa(mpa)
                .genres(List.of(genre))
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
                .genres(List.of(genre1, genre2))
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
        assertEquals(1, films.get(0).getGenres().size());
        assertEquals(2, films.get(0).getGenres().get(0).getId());
        assertEquals("Драма", films.get(0).getGenres().get(0).getName());
    }

    @Test
    void getById() {
        Film film = filmDbStorage.create(firstFilm);
        Integer filmId = film.getId();
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
        assertEquals(1, filmOptional.get().getGenres().size());
        assertEquals(2, filmOptional.get().getGenres().get(0).getId());
        assertEquals("Драма", filmOptional.get().getGenres().get(0).getName());
    }

    @Test
    void create() {
        Film film = filmDbStorage.create(firstFilm);
        Integer filmId = film.getId();

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
        assertEquals(1, filmOptional.get().getGenres().size());
        assertEquals(2, filmOptional.get().getGenres().get(0).getId());
        assertEquals("Драма", filmOptional.get().getGenres().get(0).getName());
    }

    @Test
    void update() {
        Film film = filmDbStorage.create(firstFilm);
        Integer filmId = film.getId();

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
        assertEquals(2, filmOptional.get().getGenres().size());
        assertEquals(1, filmOptional.get().getGenres().get(0).getId());
        assertEquals("Комедия", filmOptional.get().getGenres().get(0).getName());
        assertEquals(3, filmOptional.get().getGenres().get(1).getId());
        assertEquals("Мультфильм", filmOptional.get().getGenres().get(1).getName());
    }

    @Test
    void addLike() {
        Film film = filmDbStorage.create(firstFilm);
        Integer filmId = film.getId();
        User user = userDbStorage.create(newUser);
        Integer userId = user.getId();

        assertTrue(film.getLikedUsers().isEmpty());

        filmDbStorage.addLike(filmId, userId);
        Optional<Film> likedFilm = filmDbStorage.getById(filmId);

        assertTrue(likedFilm.isPresent());
        assertEquals(1, likedFilm.get().getLikedUsers().size());
        assertEquals(userId, likedFilm.get().getLikedUsers().stream().findAny().orElse(null));
    }

    @Test
    void deleteLike() {
        Film film = filmDbStorage.create(firstFilm);
        Integer filmId = film.getId();
        User user = userDbStorage.create(newUser);
        Integer userId = user.getId();

        assertTrue(film.getLikedUsers().isEmpty());

        filmDbStorage.addLike(filmId, userId);
        Optional<Film> likedFilm = filmDbStorage.getById(filmId);
        filmDbStorage.deleteLike(filmId, userId);
        Optional<Film> deletedLikeFilm = filmDbStorage.getById(filmId);

        assertTrue(likedFilm.isPresent());
        assertEquals(1, likedFilm.get().getLikedUsers().size());
        assertEquals(userId, likedFilm.get().getLikedUsers().stream().findAny().orElse(null));
        assertTrue(deletedLikeFilm.isPresent());
        assertEquals(0, deletedLikeFilm.get().getLikedUsers().size());
    }

    @Test
    void getPopularFilms() {
        Film film1 = filmDbStorage.create(firstFilm);
        Integer filmId1 = film1.getId();

        Film film2 = filmDbStorage.create(secondFilm);
        Integer filmId2 = film2.getId();

        User user = userDbStorage.create(newUser);
        Integer userId = user.getId();

        List<Film> popularFilmBeforeLike = filmDbStorage.getPopularFilms(2);
        filmDbStorage.addLike(filmId2, userId);
        List<Film> popularFilmAfterLike = filmDbStorage.getPopularFilms(2);

        assertEquals(2, popularFilmBeforeLike.size());
        assertEquals(filmId1, popularFilmBeforeLike.get(0).getId());
        assertEquals(0, popularFilmBeforeLike.get(0).getLikedUsers().size());
        assertEquals(filmId2, popularFilmBeforeLike.get(1).getId());
        assertEquals(0, popularFilmBeforeLike.get(1).getLikedUsers().size());
        assertEquals(2, popularFilmAfterLike.size());
        assertEquals(filmId2, popularFilmAfterLike.get(0).getId());
        assertEquals(1, popularFilmAfterLike.get(0).getLikedUsers().size());
        assertEquals(userId, popularFilmAfterLike.get(0).getLikedUsers().stream().findAny().orElse(null));
        assertEquals(filmId1, popularFilmAfterLike.get(1).getId());
        assertEquals(0, popularFilmAfterLike.get(1).getLikedUsers().size());
    }
}