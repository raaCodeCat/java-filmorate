package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.mparating.MpaRatingStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Сервис для {@link Film}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;

    private final FilmGenreStorage filmGenreStorage;

    private final FilmLikeStorage filmLikeStorage;

    private final MpaRatingStorage mpaRatingStorage;

    private final GenreService genreService;

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
    @Transactional
    public Film create(Film film) {
        MpaRating mpaRating = film.getMpa();
        Set<Genre> genres = film.getGenres();

        checkExistsMpa(mpaRating);
        checkExistsGenre(genres);

        log.debug("Добавляем фильм {}.", film);
        Integer filmId = filmStorage.create(film);

        if (genres != null && genres.size() > 0) {
            log.debug("Добавляем жанры фильма {}.", film);
            filmGenreStorage.addFilmGenres(filmId, genres);
        }

        return filmStorage.getById(filmId).orElse(null);
    }

    @Override
    public Film update(Film film) {
        log.debug("Попытка обновить фильм {}.", film);

        Integer filmId = film.getId();
        Set<Genre> genres = film.getGenres();
        MpaRating mpaRating = film.getMpa();

        checkExistsFilmById(filmId);
        checkExistsMpa(mpaRating);
        checkExistsGenre(genres);

        log.debug("Обновляем фильм {}.", film);
        filmStorage.update(filmId, film);

        filmGenreStorage.deleteFilmGenres(filmId);

        if (genres != null && genres.size() > 0) {
            log.debug("Обновляем жанры фильма {}.", film);
            filmGenreStorage.addFilmGenres(filmId, genres);
        }

        return filmStorage.getById(filmId).orElse(null);
    }

    @Override
    public Integer addLike(Integer id, Integer userId) {
        checkExistsFilmById(id);
        checkExistsUserById(userId);

        return filmLikeStorage.addLike(id, userId);
    }

    @Override
    public Integer deleteLike(Integer id, Integer userId) {
        checkExistsFilmById(id);
        checkExistsUserById(userId);

        return filmLikeStorage.deleteLike(id, userId);
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

    private void checkExistsMpa(MpaRating mpa) {
        if (mpa == null || mpa.getId() == null) {
            return;
        }

        Integer id = mpa.getId();

        Optional<MpaRating> optionalMpaRating = mpaRatingStorage.getById(id);
        if (optionalMpaRating.isEmpty()) {
            throw new BadRequestException(
                    HttpStatus.BAD_REQUEST, "MPA рейтинг с id = " + id + " отсутствует"
            );
        }
    }

    private void checkExistsGenre(Set<Genre> genres) {
        if (genres == null || genres.size() == 0) {
            return;
        }

        List<Genre> genresInDb = genreService.get();
        List<Integer> genresInDbIds = new ArrayList<>();

        for (Genre g : genresInDb) {
            genresInDbIds.add(g.getId());
        }

        for (Genre genre : genres) {
            if (!genresInDbIds.contains(genre.getId())) {
                throw new BadRequestException(
                        HttpStatus.BAD_REQUEST, "Жанр с id = " + genre.getId() + " отсутствует"
                );
            }
        }
    }
}
