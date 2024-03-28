package ru.yandex.practicum.filmorate.storage.filmgenre;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Set;

public interface FilmGenreStorage {
    void addFilmGenres(Integer filmId, Set<Genre> genres);

    void deleteFilmGenres(Integer filmId);
}
