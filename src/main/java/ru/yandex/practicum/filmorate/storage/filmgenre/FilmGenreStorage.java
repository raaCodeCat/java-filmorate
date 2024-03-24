package ru.yandex.practicum.filmorate.storage.filmgenre;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;

public interface FilmGenreStorage {
    void addFilmGenres(Integer filmId, List<Genre> genres);

    void deleteFilmGenres(Integer filmId);
}
