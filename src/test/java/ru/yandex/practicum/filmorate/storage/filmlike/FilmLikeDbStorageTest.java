package ru.yandex.practicum.filmorate.storage.filmlike;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmLikeDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;
    private UserDbStorage userDbStorage;
    private FilmLikeDbStorage filmLikeDbStorage;
    private Film newFilm;
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

        filmLikeDbStorage = new FilmLikeDbStorage(jdbcTemplate);

        MpaRating mpa =  MpaRating.builder().id(1).build();
        Genre genre = Genre.builder().id(2).build();
        newFilm = Film.builder()
                .name("FilmName")
                .description("FilmDescription")
                .releaseDate(LocalDate.of(2020, 1, 2))
                .duration(100)
                .mpa(mpa)
                .genres(List.of(genre))
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
        Film film = filmDbStorage.create(newFilm);
        Integer filmId = film.getId();
        User user = userDbStorage.create(newUser);
        Integer userId = user.getId();

        filmDbStorage.addLike(filmId, userId);
        List<FilmLike> filmLikes = filmLikeDbStorage.get();

        assertEquals(1, filmLikes.size());
        assertEquals(filmId, filmLikes.get(0).getFilm().getId());
        assertEquals(userId, filmLikes.get(0).getUser().getId());
    }

    @Test
    void getLikedUsersByFilmId() {
        Film film = filmDbStorage.create(newFilm);
        Integer filmId = film.getId();
        User user = userDbStorage.create(newUser);
        Integer userId = user.getId();

        filmDbStorage.addLike(filmId, userId);
        List<User> users = filmLikeDbStorage.getLikedUsersByFilmId(filmId);

        assertEquals(1, users.size());
        assertEquals(userId, users.get(0).getId());
        assertEquals("mail@mail.ru", users.get(0).getEmail());
        assertEquals("dolore", users.get(0).getLogin());
        assertEquals("Nick Name", users.get(0).getName());
        assertEquals(LocalDate.of(1946, 8, 20), users.get(0).getBirthday());
    }

    @Test
    void getLikesCountByFilmId() {
        Film film = filmDbStorage.create(newFilm);
        Integer filmId = film.getId();
        User user = userDbStorage.create(newUser);
        Integer userId = user.getId();

        Integer likesCountBeforeLike = filmLikeDbStorage.getLikesCountByFilmId(filmId);
        filmDbStorage.addLike(filmId, userId);
        Integer likesCountAfterLike = filmLikeDbStorage.getLikesCountByFilmId(filmId);

        assertEquals(0, likesCountBeforeLike);
        assertEquals(1, likesCountAfterLike);
    }

    @Test
    void getLikedUsersIdByFilmId() {
        Film film = filmDbStorage.create(newFilm);
        Integer filmId = film.getId();
        User user = userDbStorage.create(newUser);
        Integer userId = user.getId();

        filmDbStorage.addLike(filmId, userId);
        Set<Integer> usersId = filmLikeDbStorage.getLikedUsersIdByFilmId(filmId);

        assertEquals(1, usersId.size());
        assertEquals(Set.of(userId), usersId);
    }
}